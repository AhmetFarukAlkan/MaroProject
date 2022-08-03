package com.MaroTekTimesheets.MaroProject;

import com.MaroTekTimesheets.MaroProject.Entity.User;
import com.MaroTekTimesheets.MaroProject.Repo.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class MaroProjectApplicationTests {
	@Autowired private UserRepository userRepository;
	@Test
	void contextLoads() {
	}
	@Test
	public void testAddNew(){
//		User user = new User();
//		user.setEmail("test@gmail.com");
//		user.setName("Test");
//
//		User savedUser = userRepository.save(user);

		//Assertions.assertThat(savedUser).isGreaterThan(0);
	}

}
