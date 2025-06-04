package com.TreadX;

import com.TreadX.user.Enum.Role;
import com.TreadX.user.entity.User;
import com.TreadX.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.logging.Logger;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAspectJAutoProxy
@EnableCaching
@EnableAsync
@EnableWebSecurity
public class TreadXApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;

	private String superAdminPassword = "Wassem7676.tn";
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(TreadXApplication.class, args);
	}
	public void run(String... args) throws Exception {
		if(userRepository.findAll().isEmpty()){

			User superAdmin = User.builder()
					.firstName("Wassem")
					.lastName("Tenbakji")
					.email("wasee.tenbakji@gmail.com")
					.password(passwordEncoder.encode(superAdminPassword))
					.role(Role.SUPER_ADMIN)
					.build();
			userRepository.save(superAdmin);
			Logger logger = Logger.getLogger(TreadXApplication.class.getName());
			logger.info("Super admin Email: " + superAdmin.getEmail());
			logger.info("Super admin Password: " + superAdmin.getPassword());

			User platformAdmin = User.builder()
					.firstName("platform")
					.lastName("admin")
					.email("admin@gmail.com")
					.password(passwordEncoder.encode(superAdminPassword))
					.role(Role.PLATFORM_ADMIN)
					.build();
			userRepository.save(platformAdmin);
			logger.info("Platform admin Email: " + platformAdmin.getEmail());
			logger.info("Platform admin Password: " + platformAdmin.getPassword());

		}
//
//			Dealer superAdmin = Dealer.builder()
//					.firstName("SuperAdmin")
//					.lastName("Wassem")
//					.email("wasee.tenbakji@gmail.com")
//					.password(passwordEncoder.encode(superAdminPassword))
//					.role(Role.SUPER_ADMIN)
//					.build();
//			dealerRepository.save(superAdmin);
//			Logger logger = Logger.getLogger(TreadXApplication.class.getName());
//			logger.info("Super admin Email: " + superAdmin.getEmail());
//			logger.info("Super admin Password: " + superAdmin.getPassword());
//
//			Dealer admin = Dealer.builder()
//					.firstName("Housin")
//					.lastName("Lounitch")
//					.email("housin.lountich@gmail.com")
//					.password(passwordEncoder.encode(superAdminPassword))
//					.role(Role.OWNER)
//					.build();
//			dealerRepository.save(superAdmin);
//			logger.info("Admin Email: " + admin.getEmail());
//			logger.info("Admin Password: " + admin.getPassword());
//		}
	}

}
