package com.studentgig.config;

import com.studentgig.model.*;
import com.studentgig.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.studentgig.service.AdminService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ProposalRepository proposalRepository;
    private final WorkRepository workRepository;
    private final ReviewRepository reviewRepository;

    public DataInitializer(UserRepository userRepository, JobRepository jobRepository,
                           ProposalRepository proposalRepository, WorkRepository workRepository,
                           ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.proposalRepository = proposalRepository;
        this.workRepository = workRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.findAll().isEmpty()) {
            if (userRepository.findByEmail(AdminService.ADMIN_EMAIL).isEmpty()) {
                createUser("StudentGig Admin", AdminService.ADMIN_EMAIL, "admin123",
                        "StudentGig", "Administration", "Admin", Arrays.asList("Management"));
            }
            return;
        }

        User rahul = createUser("Rahul Sharma", "rahul@studentgig.com", "rahul123",
                "IIT Delhi", "Computer Science", "3rd Year",
                Arrays.asList("Java", "Spring Boot", "React"));
        User priya = createUser("Priya Patel", "priya@studentgig.com", "priya123",
                "NIT Surat", "Information Technology", "4th Year",
                Arrays.asList("HTML", "CSS", "JavaScript", "UI Design"));
        User amit = createUser("Amit Kumar", "amit@studentgig.com", "amit123",
                "BITS Pilani", "Electronics", "2nd Year",
                Arrays.asList("Python", "Data Analysis", "Excel"));
        User sneha = createUser("Sneha Reddy", "sneha@studentgig.com", "sneha123",
                "Anna University", "Graphic Design", "3rd Year",
                Arrays.asList("Photoshop", "Illustrator", "Logo Design"));
        User vikram = createUser("Vikram Singh", "vikram@studentgig.com", "vikram123",
                "Delhi University", "Commerce", "4th Year",
                Arrays.asList("Content Writing", "SEO", "Social Media"));
        User arjun = createUser("Arjun Mehta", "arjun@studentgig.com", "arjun123",
                "TCS", "Software Development", "Working Professional",
                Arrays.asList("Java", "AWS", "Project Management"));
        User kavita = createUser("Kavita Nair", "kavita@studentgig.com", "kavita123",
                "Self-employed", "Digital Marketing", "Freelancer",
                Arrays.asList("SEO", "Google Ads", "Content Strategy"));
        User meena = createUser("Meena Iyer", "meena@studentgig.com", "meena123",
                "Infosys", "Human Resources", "Business Owner",
                Arrays.asList("Recruitment", "HR", "Training"));
        createUser("StudentGig Admin", AdminService.ADMIN_EMAIL, "admin123",
                "StudentGig", "Administration", "Admin", Arrays.asList("Management"));

        priya.setRating(4.8);
        priya.setTotalReviews(3);
        amit.setRating(4.5);
        amit.setTotalReviews(2);
        sneha.setRating(4.6);
        sneha.setTotalReviews(2);
        arjun.setRating(4.7);
        arjun.setTotalReviews(4);
        kavita.setRating(4.9);
        kavita.setTotalReviews(5);
        userRepository.save(priya);
        userRepository.save(amit);
        userRepository.save(sneha);
        userRepository.save(arjun);
        userRepository.save(kavita);

        List<User> clients = Arrays.asList(rahul, priya, amit, sneha, vikram, arjun, kavita, meena);
        List<User> freelancers = Arrays.asList(priya, amit, sneha, vikram, rahul, arjun, kavita);

        // 50 unique jobs: title, description, category, skills, budget, deadline, status
        Object[][] jobsData = {
            {"Build a Student Portfolio Website", "Need a responsive portfolio with HTML, CSS and JavaScript.", "Web Development", "HTML,CSS,JavaScript", 2500, "2026-09-15", JobStatus.OPEN},
            {"Logo Design for College Fest", "Create a modern logo for our annual tech fest.", "Graphic Design", "Photoshop,Illustrator", 1500, "2026-09-10", JobStatus.OPEN},
            {"Java REST API Development", "Build REST APIs using Spring Boot for a student project.", "Coding", "Java,Spring Boot", 5000, "2026-09-20", JobStatus.IN_PROGRESS},
            {"Social Media Marketing Campaign", "Run a 2-week social media campaign for our startup.", "Social Media", "Social Media,Content Writing", 3000, "2026-09-25", JobStatus.IN_PROGRESS},
            {"Data Entry for Research Project", "Enter survey data into Excel spreadsheets accurately.", "Data Entry", "Excel,Data Entry", 1200, "2026-09-05", JobStatus.SUBMITTED},
            {"Tutoring - Data Structures", "Help with DSA concepts and problem solving.", "Tutoring", "Java,Algorithms", 2000, "2026-08-20", JobStatus.COMPLETED},
            {"Video Editing for YouTube", "Edit a 10-minute educational video with transitions.", "Video Editing", "Premiere Pro,After Effects", 3500, "2026-08-15", JobStatus.COMPLETED},
            {"Photography for College Event", "Cover a one-day college cultural event.", "Photography", "Photography,Lightroom", 4000, "2026-09-01", JobStatus.CANCELLED},
            {"React Dashboard for Analytics", "Build an admin dashboard with charts and tables using React.", "Web Development", "React,JavaScript,CSS", 4500, "2026-09-18", JobStatus.OPEN},
            {"Python Script for Web Scraping", "Write a Python script to scrape product prices from e-commerce sites.", "Coding", "Python,BeautifulSoup", 2800, "2026-09-12", JobStatus.OPEN},
            {"Instagram Reels Editing", "Edit 5 Instagram reels with captions and music sync.", "Video Editing", "CapCut,Premiere Pro", 1800, "2026-09-08", JobStatus.OPEN},
            {"Blog Article Writing - Tech", "Write 3 SEO-optimized blog posts about AI and machine learning.", "Writing", "Content Writing,SEO", 2200, "2026-09-14", JobStatus.OPEN},
            {"Math Tutoring for JEE", "Weekly tutoring sessions for JEE mathematics preparation.", "Tutoring", "Mathematics,Teaching", 3500, "2026-10-01", JobStatus.OPEN},
            {"PowerPoint for Business Pitch", "Design a 15-slide investor pitch deck with animations.", "Presentation", "PowerPoint,Design", 1600, "2026-09-11", JobStatus.OPEN},
            {"Mobile App UI Design", "Design UI screens for a food delivery mobile app in Figma.", "Graphic Design", "Figma,UI Design", 3200, "2026-09-22", JobStatus.OPEN},
            {"C++ Assignment Help", "Help complete a C++ data structures assignment with explanations.", "Coding", "C++,Algorithms", 1500, "2026-09-09", JobStatus.OPEN},
            {"Product Photo Editing", "Edit 20 product photos for an e-commerce listing.", "Photography", "Photoshop,Lightroom", 2000, "2026-09-13", JobStatus.OPEN},
            {"LinkedIn Profile Optimization", "Rewrite and optimize LinkedIn profile for job seekers.", "Writing", "Copywriting,LinkedIn", 900, "2026-09-07", JobStatus.OPEN},
            {"Database Design for Library System", "Design MySQL schema and ER diagram for a library management system.", "Coding", "SQL,Database Design", 2400, "2026-09-16", JobStatus.OPEN},
            {"College Magazine Layout Design", "Design layout for a 20-page college magazine.", "Graphic Design", "InDesign,Illustrator", 3800, "2026-09-28", JobStatus.OPEN},
            {"YouTube Thumbnail Design Pack", "Create 10 eye-catching YouTube thumbnails for a tech channel.", "Graphic Design", "Photoshop,Canva", 1200, "2026-09-06", JobStatus.OPEN},
            {"Resume and Cover Letter Writing", "Write a professional resume and cover letter for IT internships.", "Writing", "Resume Writing,HR", 800, "2026-09-05", JobStatus.OPEN},
            {"WordPress Blog Setup", "Set up a WordPress blog with custom theme and 5 posts.", "Web Development", "WordPress,PHP,CSS", 3000, "2026-09-19", JobStatus.OPEN},
            {"Excel Dashboard for Sales Data", "Build an interactive Excel dashboard with pivot tables and charts.", "Data Entry", "Excel,Pivot Tables", 1700, "2026-09-10", JobStatus.OPEN},
            {"Physics Lab Report Writing", "Write detailed lab reports for 4 physics experiments.", "Writing", "Physics,Report Writing", 1400, "2026-09-17", JobStatus.OPEN},
            {"TikTok Content Strategy", "Create a 30-day TikTok content calendar for a fashion brand.", "Social Media", "TikTok,Marketing", 2500, "2026-09-21", JobStatus.OPEN},
            {"Android App Bug Fixing", "Fix 5 bugs in an existing Android app built with Java.", "Coding", "Java,Android", 4200, "2026-09-24", JobStatus.OPEN},
            {"Event Poster Design", "Design promotional posters for a college hackathon event.", "Graphic Design", "Photoshop,Illustrator", 1100, "2026-09-08", JobStatus.OPEN},
            {"English Essay Proofreading", "Proofread and edit 3 academic essays for grammar and clarity.", "Writing", "English,Proofreading", 600, "2026-09-06", JobStatus.OPEN},
            {"Node.js Backend API", "Build a Node.js REST API with authentication for a notes app.", "Coding", "Node.js,Express,MongoDB", 5500, "2026-09-30", JobStatus.OPEN},
            {"Graduation Photo Retouching", "Retouch 30 graduation photos with color correction.", "Photography", "Lightroom,Photoshop", 2200, "2026-09-15", JobStatus.OPEN},
            {"Chemistry Tutoring Sessions", "Online tutoring for organic chemistry for 2nd year students.", "Tutoring", "Chemistry,Teaching", 2800, "2026-10-05", JobStatus.OPEN},
            {"Research Paper Formatting", "Format a research paper in IEEE style with references.", "Writing", "LaTeX,Academic Writing", 1300, "2026-09-12", JobStatus.OPEN},
            {"E-commerce Website Frontend", "Build the frontend of an online store with cart functionality.", "Web Development", "HTML,CSS,JavaScript", 4800, "2026-09-26", JobStatus.OPEN},
            {"Podcast Audio Editing", "Edit and mix a 45-minute podcast episode.", "Video Editing", "Audacity,Adobe Audition", 1900, "2026-09-11", JobStatus.OPEN},
            {"Survey Data Collection", "Collect 100 survey responses and organize in Google Sheets.", "Data Entry", "Google Sheets,Research", 1500, "2026-09-14", JobStatus.OPEN},
            {"Flutter Mobile App Development", "Build a simple todo app using Flutter and Firebase.", "Coding", "Flutter,Dart,Firebase", 6000, "2026-10-10", JobStatus.OPEN},
            {"Brand Identity Package", "Create logo, color palette, and brand guidelines for a startup.", "Graphic Design", "Branding,Illustrator", 5000, "2026-09-27", JobStatus.OPEN},
            {"Facebook Ads Management", "Set up and manage Facebook ad campaigns for 2 weeks.", "Social Media", "Facebook Ads,Marketing", 3500, "2026-09-20", JobStatus.OPEN},
            {"Machine Learning Model Training", "Train a classification model on a provided dataset.", "Coding", "Python,Scikit-learn", 7000, "2026-10-15", JobStatus.OPEN},
            {"Wedding Invitation Design", "Design digital wedding invitations with RSVP link.", "Graphic Design", "Canva,Illustrator", 1800, "2026-09-09", JobStatus.OPEN},
            {"Technical Documentation Writing", "Write API documentation for a Spring Boot project.", "Writing", "Technical Writing,API", 2100, "2026-09-18", JobStatus.OPEN},
            {"College Website Redesign", "Redesign the homepage and navigation of a college website.", "Web Development", "HTML,CSS,Bootstrap", 5500, "2026-09-29", JobStatus.OPEN},
            {"Statistical Analysis in R", "Perform statistical analysis on survey data using R.", "Coding", "R,Statistics", 3200, "2026-09-22", JobStatus.OPEN},
            {"Product Demo Video Creation", "Create a 2-minute product demo video with voiceover.", "Video Editing", "After Effects,Premiere Pro", 4000, "2026-09-25", JobStatus.OPEN},
            {"Email Marketing Templates", "Design 5 responsive HTML email templates.", "Web Development", "HTML,CSS,Email Design", 2300, "2026-09-16", JobStatus.OPEN},
            {"Competitive Exam Coaching Notes", "Prepare summarized study notes for GATE CS exam.", "Tutoring", "Computer Science,GATE", 2600, "2026-10-01", JobStatus.OPEN},
            {"Inventory Data Migration", "Migrate 500 product records from CSV to a new format.", "Data Entry", "Excel,CSV,Data Entry", 1000, "2026-09-07", JobStatus.OPEN},
            {"Game Character Sprite Design", "Design 8 pixel art character sprites for a 2D game.", "Graphic Design", "Pixel Art,Photoshop", 3400, "2026-09-23", JobStatus.OPEN},
            {"SEO Audit for Website", "Perform complete SEO audit and provide improvement report.", "Social Media", "SEO,Google Analytics", 2700, "2026-09-19", JobStatus.OPEN},
        };

        Job[] savedJobs = new Job[jobsData.length];
        for (int i = 0; i < jobsData.length; i++) {
            Object[] d = jobsData[i];
            Long clientId = clients.get(i % clients.size()).getId();
            List<String> skills = Arrays.asList(((String) d[3]).split(","));
            Job job = createJob((String) d[0], (String) d[1], (String) d[2], skills,
                    ((Number) d[4]).doubleValue(), (String) d[5], clientId, (JobStatus) d[6]);
            savedJobs[i] = job;
        }

        // Assign freelancers to IN_PROGRESS, SUBMITTED, COMPLETED jobs
        int fIdx = 0;
        for (Job job : savedJobs) {
            if (job.getStatus() == JobStatus.IN_PROGRESS || job.getStatus() == JobStatus.SUBMITTED) {
                User freelancer = freelancers.get(fIdx % freelancers.size());
                while (freelancer.getId().equals(job.getClientId())) {
                    fIdx++;
                    freelancer = freelancers.get(fIdx % freelancers.size());
                }
                job.setFreelancerId(freelancer.getId());
                jobRepository.save(job);
                fIdx++;
            }
            if (job.getStatus() == JobStatus.COMPLETED) {
                User freelancer = freelancers.get(fIdx % freelancers.size());
                while (freelancer.getId().equals(job.getClientId())) {
                    fIdx++;
                    freelancer = freelancers.get(fIdx % freelancers.size());
                }
                job.setFreelancerId(freelancer.getId());
                job.setCompletedAt(LocalDateTime.now().minusDays((long) (Math.random() * 15) + 1));
                jobRepository.save(job);
                fIdx++;
            }
        }

        // Proposals on first 15 OPEN jobs
        int openCount = 0;
        for (Job job : savedJobs) {
            if (job.getStatus() != JobStatus.OPEN) continue;
            if (openCount >= 15) break;
            int proposals = 1 + (openCount % 3);
            for (int p = 0; p < proposals; p++) {
                User freelancer = freelancers.get((openCount + p) % freelancers.size());
                if (freelancer.getId().equals(job.getClientId())) continue;
                double price = job.getBudget() * (0.8 + Math.random() * 0.15);
                createProposal(job.getId(), freelancer.getId(), Math.round(price),
                        3 + p * 2, "I am interested in this project and can deliver quality work.", ProposalStatus.PENDING);
            }
            openCount++;
        }

        // Accepted/rejected proposals for IN_PROGRESS jobs
        for (Job job : savedJobs) {
            if (job.getStatus() != JobStatus.IN_PROGRESS) continue;
            createProposal(job.getId(), job.getFreelancerId(), job.getBudget() * 0.95, 7,
                    "Hired for this project.", ProposalStatus.ACCEPTED);
            User other = freelancers.get(0);
            if (!other.getId().equals(job.getFreelancerId()) && !other.getId().equals(job.getClientId())) {
                createProposal(job.getId(), other.getId(), job.getBudget() * 0.85, 10,
                        "Also interested in this role.", ProposalStatus.REJECTED);
            }
        }

        // Work submissions for SUBMITTED jobs
        for (Job job : savedJobs) {
            if (job.getStatus() != JobStatus.SUBMITTED) continue;
            WorkSubmission ws = new WorkSubmission();
            ws.setJobId(job.getId());
            ws.setFreelancerId(job.getFreelancerId());
            ws.setWorkLink("https://drive.google.com/demo-submission-" + job.getId());
            ws.setMessage("Completed the requested work. Please review.");
            ws.setStatus(WorkStatus.SUBMITTED);
            ws.setSubmittedAt(LocalDateTime.now().minusDays(1));
            workRepository.save(ws);
        }

        // Reviews for completed jobs
        for (Job job : savedJobs) {
            if (job.getStatus() != JobStatus.COMPLETED) continue;
            Review r = new Review();
            r.setJobId(job.getId());
            r.setReviewerId(job.getClientId());
            r.setReviewedUserId(job.getFreelancerId());
            r.setRating(4 + (int) (Math.random() * 2));
            r.setComment("Great work on \"" + job.getTitle() + "\". Delivered on time and with good quality.");
            r.setCreatedAt(job.getCompletedAt().plusDays(1));
            reviewRepository.save(r);
        }
    }

    private User createUser(String name, String email, String password, String college,
                            String department, String year, List<String> skills) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setCollege(college);
        user.setDepartment(department);
        user.setYear(year);
        user.setSkills(skills);
        user.setRating(0);
        user.setTotalReviews(0);
        user.setBlocked(false);
        return userRepository.save(user);
    }

    private Job createJob(String title, String desc, String category, List<String> skills,
                          double budget, String deadline, Long clientId, JobStatus status) {
        Job job = new Job();
        job.setTitle(title);
        job.setDescription(desc);
        job.setCategory(category);
        job.setSkillsRequired(skills);
        job.setBudget(budget);
        job.setDeadline(deadline);
        job.setClientId(clientId);
        job.setStatus(status);
        job.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 20) + 1));
        return jobRepository.save(job);
    }

    private void createProposal(Long jobId, Long freelancerId, double price, int days,
                                String message, ProposalStatus status) {
        Proposal p = new Proposal();
        p.setJobId(jobId);
        p.setFreelancerId(freelancerId);
        p.setProposedPrice(price);
        p.setDeliveryDays(days);
        p.setCoverMessage(message);
        p.setStatus(status);
        p.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 5) + 1));
        proposalRepository.save(p);
    }
}
