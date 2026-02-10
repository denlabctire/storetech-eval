# Canadian Tire Store Tech - Technical Interview

## Overview
This is a technical evaluation project for Canadian Tire Store Tech candidates. The project is a Spring Boot application with JPA, Liquibase, and H2 database.

## Technical Interview Setup Instructions

### Prerequisites
Before the interview, please complete the following steps to set up your development environment.

### Step 1: Create a GitHub Account
If you don't already have a GitHub account:

1. Go to [github.com](https://github.com)
2. Click **Sign up** in the top right corner
3. Follow the registration process (provide email, create password, choose username)
4. Verify your email address

This should take about 2-3 minutes.

### Step 2: Fork the Repository

1. Navigate to the interview repository: [https://github.com/denlabctire/storetech-eval](https://github.com/denlabctire/storetech-eval)
2. Click the **Fork** button in the top right corner
3. Select your account as the destination for the fork
4. Wait for GitHub to create your fork (this takes a few seconds)

### Step 3: Clone Your Fork Locally

Open your terminal and run:

```bash
git clone https://github.com/YOUR_USERNAME/storetech-eval.git
cd storetech-eval
```

Replace `YOUR_USERNAME` with your actual GitHub username.

### Step 4: Set Up Your Development Environment

**Requirements:**
- Java 21
- Maven 3.6+
- Docker (for running tests with Testcontainers)

**Verify your setup:**
```bash
java -version
mvn -version
docker --version
```

**Build the project:**
```bash
mvn clean install
```

**Run the application:**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Step 5: Make Your Changes

1. Work on the coding challenge as instructed [SCREENING_INSTRUCTIONS.md](SCREENING_INSTRUCTIONS.md)
2. Test your changes locally
3. Commit your changes:

```bash
git add .
git commit -m "Implement solution for technical challenge"
```

### Step 6: Push to Your Fork

```bash
git push origin main
```

If this is your first push, you wil be prompted to authenticate. 
The easiest way to authenticate is to use a Personal Access Token (PAT):
1. Go to [GitHub Settings](https://github.com/settings/profile)
2. Click on **Developer settings** in the left sidebar (at the bottom).
3. Click on **Personal access tokens** in the left sidebar.
4. Click on **Generate new classic token**.
5. Give your token a name (e.g., "Store Tech Interview Token").
6. Select the scopes you need (for pushing code, you typically need `repo` scope).
7. Click **Generate token** at the bottom of the page.
8. Save the generated token somewhere secure (you won't be able to see it again).
9. Use this token when you authenticate in the terminal. Your username is your GitHub username, and the password is the Personal Access Token you just generated.



### Step 7: Create a Pull Request

1. Go to your forked repository on GitHub: `https://github.com/YOUR_USERNAME/storetech-eval`
2. You should see a banner saying **"This branch is X commits ahead of denlabctire:main"**
3. Click the **Contribute** button, then **Open pull request**
4. Add a title and description explaining your changes
5. Click **Create pull request**

## Project Structure

```
storetech-eval/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
├── README.md
├── IMPLEMENTATION.md
├── SCREENING_INSTRUCTIONS.md
├── build_config/
│   ├── checkstyle.xml
│   └── suppressions.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/cantire/storetech/evaluation/
    │   │       ├── Application.java
    │   │       ├── controller/
    │   │       │   ├── CartController.java
    │   │       │   └── ProductController.java
    │   │       ├── dto/
    │   │       │   ├── CartSaveRequest.java
    │   │       │   ├── CartSaveResponse.java
    │   │       │   └── ProductResponse.java
    │   │       ├── model/
    │   │       │   ├── Cart.java
    │   │       │   ├── PriceInfo.java
    │   │       │   ├── Product.java
    │   │       │   ├── ProductCategory.java
    │   │       │   └── TaxInfo.java
    │   │       ├── repo/
    │   │       │   ├── CartRepository.java
    │   │       │   ├── PriceInfoRepository.java
    │   │       │   ├── ProductRepository.java
    │   │       │   └── TaxInfoRepository.java
    │   │       └── service/
    │   │           ├── CartService.java
    │   │           ├── CartServiceImpl.java
    │   │           ├── ProductService.java
    │   │           ├── ProductServiceImpl.java
    │   │           ├── TaxService.java
    │   │           └── TaxServiceImpl.java

    │   └── resources/
    │       ├── application.yml
    │       └── db.changelog/
    │           ├── db.changelog-master.xml
    │           ├── changelog-1.0-initial-schema.xml
    │           └── changelog-2.0-sample-data.xml
    └── test/
        ├── java/
        │   └── com/cantire/storetech/evaluation/
        │       ├── ApplicationTests.java
        │       └── service/
        │           ├── CartServiceTest.java
        │           ├── ProductServiceTest.java
        │           └── TaxServiceTest.java        
        └── resources/
            └── application.yml
```

## Technologies Used

- **Spring Boot 3.5.10** - Application framework
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database
- **Liquibase** - Database schema management
- **Testcontainers** - Integration testing
- **Lombok** - Reduce boilerplate code
- **JUnit 5** - Testing framework

## Database Schema

The application uses Liquibase to manage database schema. The schema includes:

## H2 Console

When the application is running, you can access the H2 database console at:
```
http://localhost:8080/h2-console
```

**Connection details:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

## Troubleshooting

- **GitHub blocked in your region?** Try using a VPN or contact the interviewer immediately
- **Authentication issues?** Make sure you're using your GitHub username and password
- **Can't push?** Verify you're pushing to your fork, not the original repository
- **Tests failing?** Ensure Docker is running (required for Testcontainers)
- **Build errors?** Make sure you're using Java 21

## Questions?

If you encounter any issues during setup, please contact the interviewer before the interview begins.

---

Good luck with your technical interview!
