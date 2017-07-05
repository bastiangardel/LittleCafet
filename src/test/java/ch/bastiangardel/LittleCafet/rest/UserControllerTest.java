package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.Application;
import ch.bastiangardel.LittleCafet.ShiroConfiguration;
import ch.bastiangardel.LittleCafet.model.Permission;
import ch.bastiangardel.LittleCafet.model.Role;
import ch.bastiangardel.LittleCafet.model.User;
import ch.bastiangardel.LittleCafet.repository.PermissionRepository;
import ch.bastiangardel.LittleCafet.repository.RoleRepository;
import ch.bastiangardel.LittleCafet.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.TestRestTemplate.HttpClientOption;
import org.springframework.http.*;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.AssertJUnit.assertEquals;

@SpringApplicationConfiguration(classes
        = {Application.class, ShiroConfiguration.class})
@WebAppConfiguration
@IntegrationTest
@TestExecutionListeners(inheritListeners = false, listeners
        = {DependencyInjectionTestExecutionListener.class})
public class UserControllerTest extends AbstractTestNGSpringContextTests {

    private final String BASE_URL = "https://localhost:9000";
    private final String USER_NAME = "Paulo Pires";
    private final String USER_EMAIL = "test@test.com";
    private final String USER_PWD = "test";
    @Autowired
    private DefaultPasswordService passwordService;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PermissionRepository permissionRepo;

    @BeforeClass
    public void setUp() {
        // clean-up users, roles and permissions
        userRepo.deleteAll();
        roleRepo.deleteAll();
        permissionRepo.deleteAll();
        // define permissions
        final Permission p1 = new Permission();
        p1.setName("VIEW_USER_ROLES");
        permissionRepo.save(p1);
        // define roles
        final Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleAdmin.getPermissions().add(p1);
        roleRepo.save(roleAdmin);
        // define user
        final User user = new User();
        user.setActive(true);
        user.setCreated(System.currentTimeMillis());
        user.setEmail(USER_EMAIL);
        user.setName(USER_NAME);
        user.setPassword(passwordService.encryptPassword(USER_PWD));
        user.getRoles().add(roleAdmin);
        userRepo.save(user);
    }

    @Test
    public void test_count() {
        assertEquals(1, userRepo.count());
    }

    @Test
    public void test_authenticate_success() throws JsonProcessingException {
        // authenticate
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(USER_EMAIL, USER_PWD));
        System.out.println(json);
        final ResponseEntity<String> response = new TestRestTemplate(
                HttpClientOption.ENABLE_COOKIES).exchange(BASE_URL.concat("/users/auth"),
                HttpMethod.POST, new HttpEntity<>(json, headers), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));


    }

    @Test
    public void test_authenticate_failure() throws JsonProcessingException {
        // authenticate
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(USER_EMAIL, "wrong password"));
        System.out.println(json);
        final ResponseEntity<String> response = new TestRestTemplate(
                HttpClientOption.ENABLE_COOKIES).exchange(BASE_URL.concat("/users/auth"),
                HttpMethod.POST, new HttpEntity<>(json, headers), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

}
