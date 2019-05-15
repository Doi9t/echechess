/*
 *    Copyright 2014 - 2018 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ca.watier.echechess.configuration.independent;

import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.components.MessageActionExecutor;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.exceptions.UserException;
import ca.watier.echechess.models.Roles;
import ca.watier.echechess.models.User;
import ca.watier.echechess.repositories.IndependentGameRepositoryImpl;
import ca.watier.echechess.repositories.IndependentUserRepositoryImpl;
import ca.watier.echechess.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("independent-mode")
public class IndependentModeConfiguration {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(IndependentModeConfiguration.class);

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public IndependentModeConfiguration(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public GameRepository<GenericGameHandler> gameRepository() {
        return new IndependentGameRepositoryImpl();
    }

    @Bean
    public MessageActionExecutor messageActionExecutor(GameRepository<GenericGameHandler> gameRepository,
                                                       WebSocketService webSocketService,
                                                       ObjectMapper objectMapper) {
        return new MessageActionExecutor(gameRepository, webSocketService, objectMapper);
    }

    @Bean
    public UserRepository userRepository() {
        IndependentUserRepositoryImpl independentUserRepositoryImpl = new IndependentUserRepositoryImpl(passwordEncoder);

        try {
            User admin = new User("admin", "admin", "adminEmail");
            User adminTwo = new User("admin2", "admin2", "adminEmail2");

            independentUserRepositoryImpl.addNewUserWithRole(admin, Roles.ADMIN);
            independentUserRepositoryImpl.addNewUserWithRole(adminTwo, Roles.ADMIN);
        } catch (UserException e) {
            LOGGER.error("Unable to create the default admin user!", e);
        }

        return independentUserRepositoryImpl;
    }
}
