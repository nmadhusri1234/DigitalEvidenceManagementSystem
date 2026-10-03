package com.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import com.dao.*;
import com.exception.*;
import com.model.*;
import com.service.*;

import com.util.FileStorageUtil;

public class Main {

    // =========================================================
    // CONSOLE COLORS
    // =========================================================

	//ANSI escape codes
	//\033 → Escape character (starts the ANSI command)
	//The [ starts the ANSI formatting command.
	//[31 → 31 means red
	//m → applies the formatting
	
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String BLUE = "\033[34m";
    private static final String MAGENTA = "\033[35m";
    private static final String CYAN = "\033[36m";

    //used for persistent storage.instead of database,storing the objects in .dat files.
    private static final String USER_FILE = "data/users.dat";
    private static final String CASE_FILE = "data/cases.dat";
    private static final String EVIDENCE_FILE = "data/evidence.dat";

    private static void printHeader(String title) {
        System.out.println();
        System.out.println(CYAN + BOLD + "======================================" + RESET);
        System.out.println(CYAN + BOLD + " " + title + RESET);
        System.out.println(CYAN + BOLD + "======================================" + RESET);
    }

    //helper methods for printing
    //instead of repeatedly writing those print statements these methods are used
    private static void printSection(String title) {
        System.out.println();
        System.out.println(BLUE + BOLD + "----------- " + title + " -----------" + RESET);
    }

    private static void printMenuOption(String option) {
        System.out.println(MAGENTA + option + RESET);
    }

    private static void printPrompt(String prompt) {
        System.out.print(YELLOW + prompt + RESET);
    }

    //prints the message in green
    //reset is important because it returns the console back to normal formatting
    private static void printSuccess(String message) {
        System.out.println(GREEN + message + RESET);
    }

    private static void printError(String message) {
        System.out.println(RED + message + RESET);
    }

    private static void printInfo(String message) {
        System.out.println(CYAN + message + RESET);
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        //create dao objects
        UserDAO userDAO = new UserDAO();
        CaseDAO caseDAO = new CaseDAO();
        EvidenceDAO evidenceDAO = new EvidenceDAO();
        AuditDAO auditDAO = new AuditDAO();

        //create service objects and passing DAO objects into service objects
        UserService userService =
                new UserService(userDAO);

        CaseService caseService =
                new CaseService(caseDAO);

        EvidenceService evidenceService =
                new EvidenceService(evidenceDAO);

        //who is user? are username password correct?
        AuthenticationService authenticationService =
                new AuthenticationService(userDAO);

//
//        What role does this user have?
//        		ADMIN?
//        		INVESTIGATOR?
//        		AUDITOR?
        AuthorizationService authorizationService =
                new AuthorizationService();

        AuditService auditService =
                new AuditService(auditDAO);

        try {

        	//this makes sure the data directory exists
            Files.createDirectories(
                    Paths.get("data")
            );

            //loads previously saved data
            loadData(
                    userDAO,
                    caseDAO,
                    evidenceDAO
            );

            createDefaultUsers(userDAO);

            boolean running = true;

            while (running) {

                printHeader("DIGITAL EVIDENCE MANAGEMENT SYSTEM");

                printMenuOption("1. Login");
                printMenuOption("2. Register");
                printMenuOption("3. Exit");

                printPrompt("Enter your choice: ");

                int choice = readInt(scanner);

                switch (choice) {

                    case 1:

                        login(
                                scanner,
                                authenticationService,
                                authorizationService,
                                userService,
                                caseService,
                                evidenceService,
                                auditService,
                                userDAO,
                                caseDAO,
                                evidenceDAO,
                                auditDAO
                        );

                        break;

                    case 2:

                        registerUser(
                                scanner,
                                userService,
                                userDAO
                        );

                        break;

                    case 3:

                        saveData(
                                userDAO,
                                caseDAO,
                                evidenceDAO
                        );

                        System.out.println(
                                "Data saved successfully."
                        );

                        System.out.println(
                                "Thank you for using DEMS."
                        );

                        running = false;

                        break;

                    default:

                        System.out.println(
                                "Invalid choice."
                        );
                }
            }

        } catch (IOException e) {

            printError(
                    "Error while accessing stored data."
            );

            System.out.println(
                    e.getMessage()
            );

        } finally {

            scanner.close();
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================

    private static void login(
            Scanner scanner,
            AuthenticationService authenticationService,
            AuthorizationService authorizationService,
            UserService userService,
            CaseService caseService,
            EvidenceService evidenceService,
            AuditService auditService,
            UserDAO userDAO,
            CaseDAO caseDAO,
            EvidenceDAO evidenceDAO,
            AuditDAO auditDAO) {

        printSection("LOGIN");

        printPrompt("Username: ");
        String username = scanner.nextLine();

        printPrompt("Password: ");
        String password = readPassword(scanner);

        try {

            User loggedInUser =
                    authenticationService.login(
                            username,
                            password
                    );

            System.out.println();
            printSuccess("Login successful.");

            printInfo(
                    "Welcome, "
                    + loggedInUser.getUserName()
                    + "!"
            );

            if (authorizationService.isAdmin(
                    loggedInUser)) {

                adminMenu(
                        scanner,
                        loggedInUser,
                        userService,
                        caseService,
                        evidenceService,
                        auditService,
                        userDAO,
                        caseDAO,
                        evidenceDAO,
                        auditDAO
                );

            } else if (
                    authorizationService.isInvestigator(
                            loggedInUser)) {

                investigatorMenu(
                        scanner,
                        loggedInUser,
                        caseService,
                        evidenceService,
                        auditService
                );

            } else if (
                    authorizationService.isAuditor(
                            loggedInUser)) {

                auditorMenu(
                        scanner,
                        loggedInUser,
                        caseService,
                        evidenceService,
                        auditService,
                                auditDAO
                );
            }

        } catch (InvalidLoginException e) {

            System.out.println();
            printError(
                    "Login failed: "
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // REGISTER
    // =========================================================

    private static void registerUser(
            Scanner scanner,
            UserService userService,
            UserDAO userDAO) {

        printSection("REGISTER");

        printPrompt("Enter username: ");
        String username = scanner.nextLine();

        if (username.trim().isEmpty()) {

            System.out.println(
                    "Username cannot be empty."
            );

            return;
        }

        printPrompt("Enter email: ");
        String email = scanner.nextLine();

        printPrompt("Enter mobile number: ");
        String mobileNumber = scanner.nextLine();

        printPrompt("Enter address: ");
        String address = scanner.nextLine();

        printPrompt("Enter password: ");
        String password = readPassword(scanner);

        printPrompt("Confirm password: ");
        String confirmPassword =
                readPassword(scanner);

        if (!password.equals(confirmPassword)) {

            System.out.println();
            System.out.println(
                    "Passwords do not match."
            );

            return;
        }

        System.out.println();
        System.out.println(
                "Select Role:"
        );

        System.out.println(
                "1. INVESTIGATOR"
        );

        System.out.println(
                "2. AUDITOR"
        );

        printPrompt("Enter choice: ");

        int roleChoice = readInt(scanner);

        Role role;

        switch (roleChoice) {

            case 1:

                role = Role.INVESTIGATOR;
                break;

            case 2:

                role = Role.AUDITOR;
                break;

            default:

                System.out.println(
                        "Invalid role choice."
                );

                return;
        }

        int userId =
                generateUserId(userDAO);

        User user = new User(
                userId,
                username,
                email,
                mobileNumber,
                address,
                password,
                role
        );

        try {

            userService.registerUser(user);

            saveUsers(userDAO);

            System.out.println();
            printSuccess("Registration completed successfully.");

            System.out.println(
                    "Your User ID: "
                    + userId
            );

            System.out.println(
                    "You can now login using your username and password."
            );

        } catch (DuplicateUserException e) {

            System.out.println();
            printError(
                    "Registration failed: "
                    + e.getMessage()
            );
        }  catch (IOException e) {
            printError("Registration successful, but user data could not be saved.");
            printError("Error: " + e.getMessage());
        }
    }
    // =========================================================
    // GENERATE USER ID
    // =========================================================

    private static int generateUserId(
            UserDAO userDAO) {

        int maxId = 0;

        for (User user : userDAO.getAll()) {

            if (user.getUserId() > maxId) {

                maxId = user.getUserId();
            }
        }

        return maxId + 1;
    }

    // =========================================================
    // ADMIN MENU
    // =========================================================

    private static void adminMenu(
            Scanner scanner,
            User loggedInUser,
            UserService userService,
            CaseService caseService,
            EvidenceService evidenceService,
            AuditService auditService,
            UserDAO userDAO,
            CaseDAO caseDAO,
            EvidenceDAO evidenceDAO,
            AuditDAO auditDAO) {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println(
                    "================================"
            );
            System.out.println(
                    "          ADMIN MENU"
            );
            System.out.println(
                    "================================"
            );

            printMenuOption("1. Manage Users");
            printMenuOption("2. View Cases");
            printMenuOption("3. View Evidence");
            printMenuOption("4. Delete Evidence");
            printMenuOption("5. View Audit Logs");
            printMenuOption("6. Logout");

            printPrompt("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    viewUsers(userDAO);
                    break;

                case 2:

                    viewCases(caseDAO);
                    break;

                case 3:

                    viewEvidence(evidenceDAO);
                    break;

                case 4:

                    deleteEvidence(
                            scanner,
                            evidenceService
                    );

                    break;

                case 5:

                    viewAuditLogs(auditDAO);
                    break;

                case 6:

                    System.out.println(
                            "Logged out successfully."
                    );

                    running = false;
                    break;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    // =========================================================
    // INVESTIGATOR MENU
    // =========================================================

    private static void investigatorMenu(
            Scanner scanner,
            User loggedInUser,
            CaseService caseService,
            EvidenceService evidenceService,
            AuditService auditService) {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println(
                    "================================"
            );
            System.out.println(
                    "       INVESTIGATOR MENU"
            );
            System.out.println(
                    "================================"
            );

            printMenuOption("1. Create Case");
            printMenuOption("2. View Cases");
            printMenuOption("3. Find Case By ID");
            printMenuOption("4. Upload Evidence");
            printMenuOption("5. View Evidence");
            printMenuOption("6. Find Evidence By ID");
            printMenuOption("7. Delete Case");
            printMenuOption("8. Delete Evidence");
            System.out.println(
                    "9. Verify Evidence Integrity"
            );
            printMenuOption("10. Logout");

            printPrompt("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    createCase(
                            scanner,
                            loggedInUser,
                            caseService
                    );

                    break;

                case 2:

                    viewCasesFromService(caseService);
                    break;

                case 3:

                    findCase(
                            scanner,
                            caseService
                    );

                    break;

                case 4:

                    uploadEvidence(
                            scanner,
                            loggedInUser,
                            caseService,
                            evidenceService
                    );

                    break;

                case 5:

                    viewEvidenceFromService(
                            evidenceService
                    );

                    break;

                case 6:

                    findEvidence(
                            scanner,
                            evidenceService
                    );

                    break;

                case 7:

                    deleteCase(
                            scanner,
                            caseService
                    );

                    break;

                case 8:

                    deleteEvidence(
                            scanner,
                            evidenceService
                    );

                    break;

                case 9:

                    verifyEvidence(
                            scanner,
                            evidenceService
                    );

                    break;

                case 10:

                    System.out.println(
                            "Logged out successfully."
                    );

                    running = false;
                    break;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    // =========================================================
    // AUDITOR MENU
    // =========================================================

    private static void auditorMenu(
            Scanner scanner,
            User loggedInUser,
            CaseService caseService,
            EvidenceService evidenceService,
            AuditService auditService,
            AuditDAO auditDAO) {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println(
                    "================================"
            );
            System.out.println(
                    "         AUDITOR MENU"
            );
            System.out.println(
                    "================================"
            );

            printMenuOption("1. View Cases");
            printMenuOption("2. View Evidence");
            printMenuOption("3. View Audit Logs");
            printMenuOption("4. Logout");

            printPrompt("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    viewCasesFromService(caseService);
                    break;

                case 2:

                    viewEvidenceFromService(
                            evidenceService
                    );

                    break;

                case 3:

                    viewAuditLogs(auditDAO);
                    break;

                case 4:

                    System.out.println(
                            "Logged out successfully."
                    );

                    running = false;
                    break;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    // =========================================================
    // CREATE CASE
    // =========================================================

    private static void createCase(
            Scanner scanner,
            User loggedInUser,
            CaseService caseService) {

        printSection("CREATE CASE");

        printPrompt("Enter Case ID: ");
        int caseId = readInt(scanner);

        printPrompt("Enter Case Title: ");
        String title = scanner.nextLine();

        printPrompt("Enter Description: ");
        String description = scanner.nextLine();

        Case caseObject = new Case(
                caseId,
                title,
                description,
                loggedInUser,
                LocalDate.now(),
                CaseStatus.OPEN
        );

        try {

            caseService.createCase(caseObject);

        } catch (DuplicateCaseException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // VIEW CASES
    // =========================================================

    private static void viewCases(
            CaseDAO caseDAO) {

        printSection("ALL CASES");

        List<Case> cases =
                caseDAO.getAll();

        if (cases.isEmpty()) {

            printSuccess("No cases found.");

            return;
        }

        for (Case caseObject : cases) {

            System.out.println(
                    caseObject
            );

            System.out.println(
                    "--------------------------------"
            );
        }
    }

    private static void viewCasesFromService(
            CaseService caseService) {

        printSection("ALL CASES");

        List<Case> cases =
                caseService.getAllCases();

        if (cases.isEmpty()) {

            printSuccess("No cases found.");
            return;
        }

        for (Case caseObject : cases) {

            System.out.println(caseObject);
            System.out.println(
                    "--------------------------------"
            );
        }
    }

    // =========================================================
    // FIND CASE
    // =========================================================

    private static void findCase(
            Scanner scanner,
            CaseService caseService) {

        System.out.print(
                "Enter Case ID: "
        );

        int caseId = readInt(scanner);

        try {

            Case caseObject =
                    caseService.getCaseById(
                            caseId
                    );

            System.out.println();
            System.out.println(
                    caseObject
            );

        } catch (CaseNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // DELETE CASE
    // =========================================================

    private static void deleteCase(
            Scanner scanner,
            CaseService caseService) {

        System.out.print(
                "Enter Case ID to delete: "
        );

        int caseId = readInt(scanner);

        try {

            caseService.deleteCase(
                    caseId
            );

        } catch (CaseNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // UPLOAD EVIDENCE
    // =========================================================

    private static void uploadEvidence(
            Scanner scanner,
            User loggedInUser,
            CaseService caseService,
            EvidenceService evidenceService) {

        printSection("UPLOAD EVIDENCE");

        System.out.print(
                "Enter Evidence ID: "
        );

        int evidenceId =
                readInt(scanner);

        System.out.print(
                "Enter Evidence Name: "
        );

        String evidenceName =
                scanner.nextLine();

        System.out.println();
        printInfo("Select Evidence Type:");

        printMenuOption("1. IMAGE");
        printMenuOption("2. VIDEO");
        printMenuOption("3. AUDIO");
        printMenuOption("4. DOCUMENT");
        printMenuOption("5. PDF");
        printMenuOption("6. TEXT");
        printMenuOption("7. OTHER");

        System.out.print(
                "Enter choice: "
        );

        int typeChoice =
                readInt(scanner);

        EvidenceType evidenceType;

        switch (typeChoice) {

            case 1:
                evidenceType = EvidenceType.IMAGE;
                break;

            case 2:
                evidenceType = EvidenceType.VIDEO;
                break;

            case 3:
                evidenceType = EvidenceType.AUDIO;
                break;

            case 4:
                evidenceType = EvidenceType.DOCUMENT;
                break;

            case 5:
                evidenceType = EvidenceType.PDF;
                break;

            case 6:
                evidenceType = EvidenceType.TEXT;
                break;

            case 7:
                evidenceType = EvidenceType.OTHER;
                break;

            default:

                System.out.println(
                        "Invalid evidence type."
                );

                return;
        }

        System.out.print(
                "Enter evidence file path: "
        );

        String filePath =
                scanner.nextLine();

        System.out.print(
                "Enter Case ID: "
        );

        int caseId =
                readInt(scanner);

        Case caseObject;

        try {

            caseObject =
                    caseService.getCaseById(
                            caseId
                    );

        } catch (CaseNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );

            return;
        }

        Evidence evidence =
                new Evidence(
                        evidenceId,
                        evidenceName,
                        evidenceType,
                        filePath,
                        loggedInUser,
                        LocalDateTime.now(),
                        caseObject,
                        null
                );

        try {

            evidenceService.uploadEvidence(
                    evidence
            );

        } catch (DuplicateEvidenceException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // VIEW EVIDENCE
    // =========================================================

    private static void viewEvidence(
            EvidenceDAO evidenceDAO) {

        printSection("ALL EVIDENCE");

        List<Evidence> evidenceList =
                evidenceDAO.getAll();

        if (evidenceList.isEmpty()) {

            printSuccess("No evidence found.");

            return;
        }

        for (Evidence evidence :
                evidenceList) {

            System.out.println(
                    evidence
            );

            System.out.println(
                    "--------------------------------"
            );
        }
    }

    private static void viewEvidenceFromService(
            EvidenceService evidenceService) {

        printSection("ALL EVIDENCE");

        List<Evidence> evidenceList =
                evidenceService.getAllEvidence();

        if (evidenceList.isEmpty()) {

            printSuccess("No evidence found.");
            return;
        }

        for (Evidence evidence : evidenceList) {

            System.out.println(evidence);
            System.out.println(
                    "--------------------------------"
            );
        }
    }

    // =========================================================
    // FIND EVIDENCE
    // =========================================================

    private static void findEvidence(
            Scanner scanner,
            EvidenceService evidenceService) {

        System.out.print(
                "Enter Evidence ID: "
        );

        int evidenceId =
                readInt(scanner);

        try {

            Evidence evidence =
                    evidenceService.getEvidenceById(
                            evidenceId
                    );

            System.out.println();
            System.out.println(
                    evidence
            );

        } catch (EvidenceNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // DELETE EVIDENCE
    // =========================================================

    private static void deleteEvidence(
            Scanner scanner,
            EvidenceService evidenceService) {

        System.out.print(
                "Enter Evidence ID to delete: "
        );

        int evidenceId =
                readInt(scanner);

        try {

            evidenceService.deleteEvidence(
                    evidenceId
            );

        } catch (EvidenceNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // VERIFY EVIDENCE
    // =========================================================

    private static void verifyEvidence(
            Scanner scanner,
            EvidenceService evidenceService) {

        System.out.print(
                "Enter Evidence ID: "
        );

        int evidenceId =
                readInt(scanner);

        try {

            boolean valid =
                    evidenceService.verifyEvidence(
                            evidenceId
                    );

            System.out.println();

            if (valid) {

                System.out.println(
                        "Evidence Integrity Verified."
                );

                System.out.println(
                        "SHA-256 hash matches."
                );

            } else {

                System.out.println(
                        "WARNING: Evidence may have been modified."
                );

                System.out.println(
                        "SHA-256 hash does not match."
                );
            }

        } catch (EvidenceNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // VIEW USERS
    // =========================================================

    private static void viewUsers(
            UserDAO userDAO) {

        printSection("ALL USERS");

        List<User> users =
                userDAO.getAll();

        if (users.isEmpty()) {

            printSuccess("No users found.");

            return;
        }

        for (User user : users) {

            System.out.println(
                    user
            );

            System.out.println(
                    "--------------------------------"
            );
        }
    }

    // =========================================================
    // VIEW AUDIT LOGS
    // =========================================================

    private static void viewAuditLogs(
            AuditDAO auditDAO) {

        printSection("AUDIT LOGS");

        if (auditDAO.getAll().isEmpty()) {

            printSuccess("No audit logs found.");

            return;
        }

        auditDAO.getAll().forEach(
                System.out::println
        );
    }

    // =========================================================
    // PASSWORD READER
    // =========================================================

    private static String readPassword(
            Scanner scanner) {

        java.io.Console console =
                System.console();

        if (console != null) {

            char[] password =
                    console.readPassword();

            return new String(password);
        }

        return scanner.nextLine();
    }

    // =========================================================
    // INTEGER INPUT
    // =========================================================

    private static int readInt(
            Scanner scanner) {

        while (true) {

            String input =
                    scanner.nextLine();

            try {

                return Integer.parseInt(
                        input.trim()
                );

            } catch (NumberFormatException e) {

                printPrompt("Please enter a valid number: ");
            }
        }
    }

    // =========================================================
    // DEFAULT USERS
    // =========================================================

    private static void createDefaultUsers(
            UserDAO userDAO) {

        if (!userDAO.getAll().isEmpty()) {
            return;
        }

        userDAO.add(
                new User(
                        1,
                        "admin",
                        "admin@dems.com",
                        "9000000001",
                        "Admin Office",
                        "admin123",
                        Role.ADMIN
                )
        );

        userDAO.add(
                new User(
                        2,
                        "investigator",
                        "investigator@dems.com",
                        "9000000002",
                        "Investigation Department",
                        "investigator123",
                        Role.INVESTIGATOR
                )
        );

        userDAO.add(
                new User(
                        3,
                        "auditor",
                        "auditor@dems.com",
                        "9000000003",
                        "Audit Department",
                        "auditor123",
                        Role.AUDITOR
                )
        );

        printSuccess("Default users created.");
    }

    // =========================================================
    // LOAD DATA
    // =========================================================

    private static void loadData(
            UserDAO userDAO,
            CaseDAO caseDAO,
            EvidenceDAO evidenceDAO)
            throws IOException {

        if (FileStorageUtil.fileExists(
                USER_FILE)) {

            userDAO.setUsers(
                    FileStorageUtil.loadList(
                            USER_FILE
                    )
            );
        }

        if (FileStorageUtil.fileExists(
                CASE_FILE)) {

            caseDAO.setCases(
                    FileStorageUtil.loadList(
                            CASE_FILE
                    )
            );
        }

        if (FileStorageUtil.fileExists(
                EVIDENCE_FILE)) {

            evidenceDAO.setEvidence(
                    FileStorageUtil.loadList(
                            EVIDENCE_FILE
                    )
            );
        }

        printSuccess("Stored data loaded successfully.");
    }

    // =========================================================
    // SAVE DATA
    // =========================================================

    private static void saveData(
            UserDAO userDAO,
            CaseDAO caseDAO,
            EvidenceDAO evidenceDAO)
            throws IOException {

        FileStorageUtil.saveList(
                userDAO.getAll(),
                USER_FILE
        );

        FileStorageUtil.saveList(
                caseDAO.getAll(),
                CASE_FILE
        );

        FileStorageUtil.saveList(
                evidenceDAO.getAll(),
                EVIDENCE_FILE
        );
    }

    // =========================================================
    // SAVE USERS ONLY
    // =========================================================

    private static void saveUsers(
            UserDAO userDAO)
            throws IOException {

        FileStorageUtil.saveList(
                userDAO.getAll(),
                USER_FILE
        );
    }
}
