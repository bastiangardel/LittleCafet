package ch.bastiangardel.LittleCafet.rest;

import ch.bastiangardel.LittleCafet.Application;
import ch.bastiangardel.LittleCafet.ShiroConfiguration;
import ch.bastiangardel.LittleCafet.model.Permission;
import ch.bastiangardel.LittleCafet.model.Role;
import ch.bastiangardel.LittleCafet.model.Transaction;
import ch.bastiangardel.LittleCafet.model.User;
import ch.bastiangardel.LittleCafet.repository.PermissionRepository;
import ch.bastiangardel.LittleCafet.repository.RoleRepository;
import ch.bastiangardel.LittleCafet.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes
        = {Application.class, ShiroConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(inheritListeners = false, listeners
        = {DependencyInjectionTestExecutionListener.class})
public class UserControllerTest extends AbstractTestNGSpringContextTests {

    private final String BASE_URL = "https://localhost:";
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

    @LocalServerPort
    private int port;

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
        user.setSolde(100.0);
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
                TestRestTemplate.HttpClientOption.ENABLE_COOKIES).exchange(BASE_URL.concat(String.valueOf(port)).concat("/users/auth"),
                POST, new HttpEntity<>(json, headers), String.class);
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
                TestRestTemplate.HttpClientOption.ENABLE_COOKIES).exchange(BASE_URL.concat(String.valueOf(port)).concat("/users/auth"),
                POST, new HttpEntity<>(json, headers), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }


    @Test
    public void test_date() throws IOException {
        // authenticate
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(USER_EMAIL, USER_PWD));
        System.out.println(json);
        final ResponseEntity<String> response = new TestRestTemplate(
                TestRestTemplate.HttpClientOption.ENABLE_COOKIES).exchange(BASE_URL.concat(String.valueOf(port)).concat("/users/auth"),
                POST, new HttpEntity<>(json, headers), String.class);

        HttpHeaders respheaders = response.getHeaders();
        String set_cookie = respheaders.getFirst(respheaders.SET_COOKIE);
        String set_cookie2 = respheaders.getFirst(respheaders.SET_COOKIE2);

        System.out.println(set_cookie);
        System.out.println(set_cookie2);


        HttpHeaders headers2 = new HttpHeaders();

        headers2.set(HttpHeaders.COOKIE,set_cookie);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL.concat(String.valueOf(port)).concat("/admin/manuallyCheck"))
                .queryParam("username", "test@test.com")
                .queryParam("idProduct", 0)
                .queryParam("number", 20);

        ResponseEntity<String> rateResponse =
                new TestRestTemplate().exchange(builder.build().encode().toUri().toString(),
                        POST, new HttpEntity<>(headers2), String.class);


        ResponseEntity<List<Transaction>> rateResponse2 =
                new TestRestTemplate().exchange(BASE_URL.concat(String.valueOf(port)).concat("/transaction/list"),
                        GET, new HttpEntity<>(headers2), new ParameterizedTypeReference<List<Transaction>>() {
                        });
        List<Transaction> transactionList = rateResponse2.getBody();


        System.out.println(transactionList.get(0).getCreated());
    }

}
