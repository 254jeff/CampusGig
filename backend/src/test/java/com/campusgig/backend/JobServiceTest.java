package com.campusgig.backend;

import com.campusgig.backend.dto.JobRequest;
import com.campusgig.backend.dto.JobResponse;
import com.campusgig.backend.entity.Job;
import com.campusgig.backend.entity.User;
import com.campusgig.backend.repository.JobRepository;
import com.campusgig.backend.repository.UserRepository;
import com.campusgig.backend.service.AuthService;
import com.campusgig.backend.service.JobService;
import com.campusgig.backend.dto.RegisterRequest;
import com.campusgig.backend.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JobServiceTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private String userEmail;

    @BeforeEach
    void setUp() {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("Job");
        req.setLastName("Creator");
        req.setEmail("jobcreator" + System.currentTimeMillis() + "@test.com");
        req.setPassword("password123");
        req.setPhone("0722222222");
        AuthResponse response = authService.register(req);
        userEmail = response.getEmail();
    }

    @Test
    void testCreateJob() {
        JobRequest request = new JobRequest();
        request.setTitle("Test Job");
        request.setDescription("Test description");
        request.setBudget(1000.0);
        request.setRemote(true);

        JobResponse response = jobService.createJob(userEmail, request);

        assertNotNull(response.getId());
        assertEquals("Test Job", response.getTitle());
        assertEquals(1000.0, response.getBudget());
        assertEquals(Job.JobStatus.OPEN, response.getStatus());
        assertTrue(response.isRemote());
    }

    @Test
    void testGetJobById() {
        JobRequest request = new JobRequest();
        request.setTitle("Find Me");
        request.setDescription("Find this job");
        request.setBudget(500.0);

        JobResponse created = jobService.createJob(userEmail, request);
        JobResponse found = jobService.getJobById(created.getId());

        assertEquals(created.getTitle(), found.getTitle());
        assertEquals(created.getBudget(), found.getBudget());
    }

    @Test
    void testGetMyJobs() {
        JobRequest req1 = new JobRequest();
        req1.setTitle("Job 1");
        req1.setDescription("Desc 1");
        req1.setBudget(100.0);

        JobRequest req2 = new JobRequest();
        req2.setTitle("Job 2");
        req2.setDescription("Desc 2");
        req2.setBudget(200.0);

        jobService.createJob(userEmail, req1);
        jobService.createJob(userEmail, req2);

        var myJobs = jobService.getMyJobs(userEmail);
        assertEquals(2, myJobs.size());
    }

    @Test
    void testDeleteJob() {
        JobRequest request = new JobRequest();
        request.setTitle("Delete Me");
        request.setDescription("Will be deleted");
        request.setBudget(300.0);

        JobResponse created = jobService.createJob(userEmail, request);
        jobService.deleteJob(created.getId(), userEmail);

        assertFalse(jobRepository.existsById(created.getId()));
    }

    @Test
    void testDeleteOtherUsersJob() {
        RegisterRequest req2 = new RegisterRequest();
        req2.setFirstName("Other");
        req2.setLastName("User");
        req2.setEmail("other" + System.currentTimeMillis() + "@test.com");
        req2.setPassword("password123");
        req2.setPhone("0733333333");
        AuthResponse other = authService.register(req2);

        JobRequest request = new JobRequest();
        request.setTitle("My Job");
        request.setDescription("My job");
        request.setBudget(400.0);

        JobResponse created = jobService.createJob(userEmail, request);

        assertThrows(RuntimeException.class, () ->
                jobService.deleteJob(created.getId(), other.getEmail()));
    }
}