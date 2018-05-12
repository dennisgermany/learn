package de.dennisbuerger.learn.javaeebatch;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository(forEntity = User.class)
public interface UserRepository extends EntityRepository<User, Long>{
	
}
