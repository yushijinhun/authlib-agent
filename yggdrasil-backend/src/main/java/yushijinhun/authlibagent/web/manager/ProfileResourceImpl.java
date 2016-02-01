package yushijinhun.authlibagent.web.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.dao.ServerIdRepository;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.model.TextureModel;
import static org.hibernate.criterion.Restrictions.conjunction;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.eqOrIsNull;
import static org.hibernate.criterion.Projections.property;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static yushijinhun.authlibagent.util.ResourceUtils.requireNonNullBody;
import static yushijinhun.authlibagent.util.RandomUtils.randomUUID;

@Component("profileResource")
public class ProfileResourceImpl implements ProfileResource {

	@Autowired
	protected SessionFactory sessionFactory;

	@Autowired
	private ServerIdRepository serveridRepo;

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public Collection<String> getProfiles(String name, String owner, Boolean banned, String skin, String cape, String elytra, TextureModel model, String serverId) {
		if (name != null && name.isEmpty()) {
			throw new BadRequestException("name is empty");
		}

		if (owner != null && owner.isEmpty()) {
			throw new BadRequestException("owner is empty");
		}

		Session session = sessionFactory.getCurrentSession();
		if (serverId == null) {
			Conjunction conjunction = conjunction();

			if (name != null) {
				conjunction.add(eq("name", name));
			}

			if (owner != null) {
				conjunction.add(eq("owner.id", owner));
			}

			if (banned != null) {
				conjunction.add(eq("banned", banned));
			}

			if (skin != null) {
				conjunction.add(eqOrIsNull("skin", emptyToNull(skin)));
			}

			if (cape != null) {
				conjunction.add(eqOrIsNull("cape", emptyToNull(cape)));
			}

			if (elytra != null) {
				conjunction.add(eqOrIsNull("elytra", emptyToNull(elytra)));
			}

			if (model != null) {
				conjunction.add(eq("textureModel", model));
			}

			@SuppressWarnings("unchecked")
			List<String> uuids = session.createCriteria(GameProfile.class).add(conjunction).setProjection(property("uuid")).setCacheable(true).list();
			return uuids;
		} else if (serverId.isEmpty()) {
			throw new BadRequestException("serverId is empty");
		} else {
			UUID profileUUID = serveridRepo.getOwner(serverId);
			if (profileUUID != null) {
				GameProfile profile = session.get(GameProfile.class, profileUUID.toString());
				if ((name == null || name.equals(profile.getName())) &&
						(owner == null || owner.equals(profile.getOwner().getId())) &&
						(banned == null || banned.equals(profile.isBanned())) &&
						(skin == null || Objects.equals(emptyToNull(skin), profile.getSkin())) &&
						(cape == null || Objects.equals(emptyToNull(cape), profile.getCape())) &&
						(elytra == null || Objects.equals(emptyToNull(elytra), profile.getElytra())) &&
						(model == null || model.equals(profile.getTextureModel()))) {
					return Collections.singleton(profile.getUuid());
				}
			}
			return Collections.emptyList();
		}

	}

	@Transactional
	@Override
	public ProfileInfo createProfile(ProfileInfo info) {
		requireNonNullBody(info);
		if (info.getUuid() == null) {
			info.setUuid(randomUUID());
		}

		Session session = sessionFactory.getCurrentSession();
		if (session.get(GameProfile.class, info.getUuid().toString()) != null) {
			throw new ConflictException("profile already exists");
		}

		GameProfile profile = new GameProfile();
		fillProfileInfo(profile, info);
		session.save(profile);

		// expire the account cache
		Account account = profile.getOwner();
		account.getProfiles().add(profile);
		session.update(account);

		return createProfileInfo(profile);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public ProfileInfo getProfileInfo(UUID uuid) {
		return createProfileInfo(lookupProfile(uuid));
	}

	@Transactional
	@Override
	public void deleteProfile(UUID uuid) {
		Session session = sessionFactory.getCurrentSession();

		GameProfile profile = lookupProfile(uuid);
		Account account = profile.getOwner();
		account.getProfiles().remove(profile);
		// expire the account cache
		session.update(account);
		session.delete(profile);
	}

	@Transactional
	@Override
	public ProfileInfo updateOrCreateProfile(UUID uuid, ProfileInfo info) {
		requireNonNullBody(info);

		Session session = sessionFactory.getCurrentSession();
		GameProfile profile = session.get(GameProfile.class, uuid.toString());
		if (profile == null) {
			profile = new GameProfile();
			profile.setUuid(uuid.toString());
		}
		fillProfileInfo(profile, info);
		session.saveOrUpdate(profile);

		return createProfileInfo(profile);
	}

	@Transactional
	@Override
	public ProfileInfo updateProfile(UUID uuid, ProfileInfo info) {
		requireNonNullBody(info);

		GameProfile profile = lookupProfile(uuid);
		fillProfileInfo(profile, info);
		sessionFactory.getCurrentSession().update(profile);

		return createProfileInfo(profile);
	}

	private ProfileInfo createProfileInfo(GameProfile profile) {
		ProfileInfo info = new ProfileInfo();
		info.setUuid(UUID.fromString(profile.getUuid()));
		info.setName(profile.getName());
		info.setOwner(profile.getOwner().getId());
		info.setBanned(profile.isBanned());
		info.setSkin(nullToEmpty(profile.getSkin()));
		info.setCape(nullToEmpty(profile.getCape()));
		info.setElytra(nullToEmpty(profile.getElytra()));
		info.setModel(profile.getTextureModel());
		return info;
	}

	private void fillProfileInfo(GameProfile profile, ProfileInfo info) {
		if (info.getUuid() != null) {
			String newUuid = info.getUuid().toString();
			if (profile.getUuid() == null) {
				profile.setUuid(newUuid);
			} else if (!profile.getUuid().equals(newUuid)) {
				// changing the uuid is not allowed
				throw new ConflictException("uuid conflict");
			}
		}

		if (info.getName() != null) {
			String name = info.getName();
			if (name.isEmpty()) {
				throw new BadRequestException("name cannot be empty");
			}

			if (!name.equals(profile.getName())) {
				// the name has changed, we need to update the name

				// check name conflict
				Session session = sessionFactory.getCurrentSession();
				GameProfile conflictProfile = (GameProfile) session.createCriteria(GameProfile.class).add(eq("name", name)).setCacheable(true).uniqueResult();
				if (conflictProfile != null) {
					throw new ConflictException("name conflict with profile " + conflictProfile.getUuid());
				}

				// update the name
				profile.setName(name);
			}
		}

		if (info.getOwner() != null) {
			String newOwnerId = info.getOwner();
			if (profile.getOwner() == null) {
				// lookup the owner
				Session session = sessionFactory.getCurrentSession();
				Account ownerAccount = session.get(Account.class, newOwnerId);
				if (ownerAccount == null) {
					throw new BadRequestException("owner not found");
				}

				// set the owner
				profile.setOwner(ownerAccount);
			} else if (!profile.getOwner().getId().equals(newOwnerId)) {
				// changing the owner is not allowed
				throw new ConflictException("owner conflict");
			}
		}

		if (info.getBanned() != null) {
			profile.setBanned(info.getBanned());
		}

		if (info.getSkin() != null) {
			profile.setSkin(emptyToNull(info.getSkin()));
		}

		if (info.getCape() != null) {
			profile.setCape(emptyToNull(info.getCape()));
		}

		if (info.getElytra() != null) {
			profile.setElytra(emptyToNull(info.getElytra()));
		}

		if (info.getModel() != null) {
			profile.setTextureModel(info.getModel());
		}

		if (profile.getOwner() == null) {
			throw new BadRequestException("owner cannot be null");
		}
		if (profile.getName() == null) {
			throw new BadRequestException("name cannot be null");
		}
	}

	private GameProfile lookupProfile(UUID uuid) {
		Session session = sessionFactory.getCurrentSession();
		GameProfile profile = session.get(GameProfile.class, uuid.toString());
		if (profile == null) {
			throw new NotFoundException();
		}
		return profile;
	}

}
