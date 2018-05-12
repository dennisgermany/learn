package de.dennisbuerger.learn.javaeebatch;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

@Stateless
public class UserService {
	@Resource(lookup = "java:jboss/datasources/Test")
	DataSource datasource;
	
	@PersistenceContext(unitName="dennis")
	EntityManager em;
	
	@Inject
	UserRepository userRepository;
	
	
	@PostConstruct
	public void foo(){
		System.out.println("init");
	}


	public void hello() {
		System.out.println("hello");
	}
	
	public void tradionalJpa() {
		try {
			em.merge(new User("Albert", "Einstein"));
			User user = em.find(User.class, 1L);
			System.out.println(user);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void useDeltaSpikeRepository() {
		try {
			userRepository.save(new User("Rolf", "Zucker"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void findall() {
		userRepository.findAll().stream().forEach(System.out::println);
	}
}
