# StudentGig - DSA Implementation

## DSA Concepts Used

### 1. ArrayList / List
Used throughout the application to maintain dynamic collections such as users, jobs, proposals, skills and other records.

### 2. HashMap / Map
Used by repositories for key-based storage and retrieval. For example, users can be retrieved using their unique ID or email.

### 3. HashSet
Used by `RecommendationService` to store a student's skills. This allows fast average-case membership checks while comparing student skills with required job skills.

### 4. Searching and Filtering
Jobs are searched and filtered by title, description, category, skills and status.

### 5. Sorting
Jobs are sorted by creation time. Recommended jobs are additionally sorted by skill-match percentage so the best matches appear first.

## Skill Matching Algorithm

1. Get the logged-in student's skills.
2. Normalize the skills to lowercase and remove unnecessary spaces.
3. Store student skills in a `HashSet`.
4. Visit each open job.
5. Compare every required job skill with the student's `HashSet`.
6. Count matching skills.
7. Calculate:

   `Match Percentage = (Matching Skills / Required Skills) * 100`

8. Sort recommendations by highest match percentage.
9. Display the best matching jobs on the Student Dashboard.

## Complexity

For a student with `S` skills and a job requiring `R` skills, skill comparison is approximately **O(S + R)** per job because HashSet membership is O(1) on average. If there are `J` open jobs, the matching phase is approximately **O(J(S + R))**, followed by sorting the recommendations.

## Why These Structures Are Suitable

- `List` is suitable because the number of jobs, users and applications can change dynamically.
- `HashMap` is suitable for fast lookup using unique keys such as IDs.
- `HashSet` is suitable for skill matching because duplicate skills are unnecessary and membership checks are fast.
- Sorting makes the recommendation results meaningful by placing stronger matches first.
