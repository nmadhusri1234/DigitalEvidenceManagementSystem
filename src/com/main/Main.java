package com.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import com.dao.AuditDAO;
import com.dao.CaseDAO;
import com.dao.EvidenceDAO;
import com.dao.UserDAO;

import com.exception.CaseNotFoundException;
import com.exception.DuplicateCaseException;
import com.exception.DuplicateEvidenceException;
import com.exception.EvidenceNotFoundException;
import com.exception.InvalidLoginException;

import com.model.AuditLog;
import com.model.Case;
import com.model.CaseStatus;
import com.model.Evidence;
import com.model.EvidenceType;
import com.model.Role;
import com.model.User;

import com.service.AuthenticationService;
import com.service.AuthorizationService;
import com.service.CaseService;
import com.service.EvidenceService;
import com.service.UserService;

import com.util.FileStorageUtil;

public class Main {

    private static final String USER_FILE =
            "data/users.dat";

    private static final String CASE_FILE =
            "data/cases.dat";

    private static final String EVIDENCE_FILE =
            "data/evidence.dat";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // =====================================================
        // CREATE DATA DIRECTORY
        // =====================================================

        try {

            Files.createDirectories(
                    Paths.get("data")
            );

        } catch (IOException e) {

            System.out.println(
                    "Unable to create data directory."
            );

            scanner.close();
            return;
        }

        // =====================================================
        // DAO OBJECTS
        // =====================================================

        UserDAO userDAO = new UserDAO();

        CaseDAO caseDAO = new CaseDAO();

        EvidenceDAO evidenceDAO =
                new EvidenceDAO();

        AuditDAO auditDAO =
                new AuditDAO();

        // =====================================================
        // LOAD DATA
        // =====================================================

        loadUsers(userDAO);

        loadCases(caseDAO);

        loadEvidence(evidenceDAO);

        // =====================================================
        // CREATE DEFAULT USERS
        // =====================================================

        if (userDAO.getAll().isEmpty()) {

            createDefaultUsers(userDAO);
        }

        // =====================================================
        // SERVICES
        // =====================================================

        UserService userService =
                new UserService(userDAO);

        AuthenticationService authenticationService =
                new AuthenticationService(userDAO);

        AuthorizationService authorizationService =
                new AuthorizationService();

        CaseService caseService =
                new CaseService(caseDAO);

        EvidenceService evidenceService =
                new EvidenceService(evidenceDAO);

        // =====================================================
        // MAIN LOOP
        // =====================================================

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println(
                    "======================================"
            );

            System.out.println(
                    " DIGITAL EVIDENCE MANAGEMENT SYSTEM"
            );

            System.out.println(
                    "======================================"
            );

            System.out.println("1. Login");
            System.out.println("2. Exit");

            System.out.print(
                    "Enter your choice: "
            );

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    System.out.print(
                            "Enter Username: "
                    );

                    String userName =
                            scanner.nextLine();

                    System.out.print(
                            "Enter Password: "
                    );

                    String password =
                            scanner.nextLine();

                    try {

                        User loggedInUser =
                                authenticationService.login(
                                        userName,
                                        password
                                );

                        System.out.println();

                        System.out.println(
                                "Login successful!"
                        );

                        System.out.println(
                                "Welcome, "
                                + loggedInUser.getUserName()
                        );

                        System.out.println(
                                "Role: "
                                + loggedInUser.getRole()
                        );

                        if (authorizationService
                                .isAdmin(loggedInUser)) {

                            adminMenu(
                                    scanner,
                                    userDAO,
                                    caseDAO,
                                    evidenceDAO,
                                    auditDAO,
                                    userService,
                                    caseService,
                                    evidenceService,
                                    loggedInUser
                            );

                        } else if (
                                authorizationService
                                        .isInvestigator(
                                                loggedInUser)) {

                            investigatorMenu(
                                    scanner,
                                    caseDAO,
                                    evidenceDAO,
                                    caseService,
                                    evidenceService,
                                    loggedInUser
                            );

                        } else if (
                                authorizationService
                                        .isAuditor(
                                                loggedInUser)) {

                            auditorMenu(
                                    scanner,
                                    caseDAO,
                                    evidenceDAO,
                                    auditDAO
                            );
                        }

                    } catch (
                            InvalidLoginException e) {

                        System.out.println(
                                "Login failed: "
                                + e.getMessage()
                        );
                    }

                    break;

                case 2:

                    saveAllData(
                            userDAO,
                            caseDAO,
                            evidenceDAO
                    );

                    System.out.println();
                    System.out.println(
                            "All data saved successfully."
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

        scanner.close();
    }

    // =====================================================
    // LOAD USERS
    // =====================================================

    private static void loadUsers(
            UserDAO userDAO) {

        if (!FileStorageUtil.fileExists(
                USER_FILE)) {

            System.out.println(
                    "No previous user data found."
            );

            return;
        }

        try {

            List<User> users =
                    FileStorageUtil.loadList(
                            USER_FILE
                    );

            userDAO.setUsers(users);

            System.out.println(
                    users.size()
                    + " user(s) loaded."
            );

        } catch (IOException e) {

            System.out.println(
                    "Unable to load users: "
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // LOAD CASES
    // =====================================================

    private static void loadCases(
            CaseDAO caseDAO) {

        if (!FileStorageUtil.fileExists(
                CASE_FILE)) {

            System.out.println(
                    "No previous case data found."
            );

            return;
        }

        try {

            List<Case> cases =
                    FileStorageUtil.loadList(
                            CASE_FILE
                    );

            caseDAO.setCases(cases);

            System.out.println(
                    cases.size()
                    + " case(s) loaded."
            );

        } catch (IOException e) {

            System.out.println(
                    "Unable to load cases: "
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // LOAD EVIDENCE
    // =====================================================

    private static void loadEvidence(
            EvidenceDAO evidenceDAO) {

        if (!FileStorageUtil.fileExists(
                EVIDENCE_FILE)) {

            System.out.println(
                    "No previous evidence data found."
            );

            return;
        }

        try {

            List<Evidence> evidenceList =
                    FileStorageUtil.loadList(
                            EVIDENCE_FILE
                    );

            evidenceDAO.setEvidence(
                    evidenceList
            );

            System.out.println(
                    evidenceList.size()
                    + " evidence record(s) loaded."
            );

        } catch (IOException e) {

            System.out.println(
                    "Unable to load evidence: "
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // SAVE ALL DATA
    // =====================================================

    private static void saveAllData(
            UserDAO userDAO,
            CaseDAO caseDAO,
            EvidenceDAO evidenceDAO) {

        try {

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

        } catch (IOException e) {

            System.out.println(
                    "Unable to save data: "
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // DEFAULT USERS
    // =====================================================

    private static void createDefaultUsers(
            UserDAO userDAO) {

        User admin = new User(
                1,
                "admin",
                "admin@gmail.com",
                "9999999999",
                "Tenali",
                "admin123",
                Role.ADMIN
        );

        User investigator = new User(
                2,
                "investigator",
                "investigator@gmail.com",
                "8888888888",
                "Vijayawada",
                "investigator123",
                Role.INVESTIGATOR
        );

        User auditor = new User(
                3,
                "auditor",
                "auditor@gmail.com",
                "7777777777",
                "Guntur",
                "auditor123",
                Role.AUDITOR
        );

        userDAO.add(admin);
        userDAO.add(investigator);
        userDAO.add(auditor);

        System.out.println(
                "Default users created."
        );
    }

    // =====================================================
    // ADMIN MENU
    // =====================================================

    private static void adminMenu(
            Scanner scanner,
            UserDAO userDAO,
            CaseDAO caseDAO,
            EvidenceDAO evidenceDAO,
            AuditDAO auditDAO,
            UserService userService,
            CaseService caseService,
            EvidenceService evidenceService,
            User loggedInUser) {

        boolean loggedIn = true;

        while (loggedIn) {

            System.out.println();
            System.out.println(
                    "========== ADMIN MENU =========="
            );

            System.out.println("1. Manage Users");
            System.out.println("2. View Cases");
            System.out.println("3. View Evidence");
            System.out.println("4. Delete Evidence");
            System.out.println("5. View Audit Logs");
            System.out.println("6. Logout");

            System.out.print(
                    "Enter your choice: "
            );

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    System.out.println();
                    System.out.println(
                            "====== USERS ======"
                    );

                    if (userDAO.getAll().isEmpty()) {

                        System.out.println(
                                "No users available."
                        );

                    } else {

                        for (User user :
                                userDAO.getAll()) {

                            System.out.println(user);
                        }
                    }

                    break;

                case 2:

                    displayCases(caseDAO);

                    break;

                case 3:

                    displayEvidence(evidenceDAO);

                    break;

                case 4:

                    System.out.print(
                            "Enter Evidence ID to delete: "
                    );

                    int evidenceId =
                            readInt(scanner);

                    try {

                        evidenceService.deleteEvidence(
                                evidenceId
                        );

                    } catch (
                            EvidenceNotFoundException e) {

                        System.out.println(
                                e.getMessage()
                        );
                    }

                    break;

                case 5:

                    displayAuditLogs(auditDAO);

                    break;

                case 6:

                    loggedIn = false;

                    System.out.println(
                            "Logged out successfully."
                    );

                    break;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    // =====================================================
    // INVESTIGATOR MENU
    // =====================================================

    private static void investigatorMenu(
            Scanner scanner,
            CaseDAO caseDAO,
            EvidenceDAO evidenceDAO,
            CaseService caseService,
            EvidenceService evidenceService,
            User loggedInUser) {

        boolean loggedIn = true;

        while (loggedIn) {

            System.out.println();
            System.out.println(
                    "====== INVESTIGATOR MENU ======"
            );

            System.out.println("1. Create Case");
            System.out.println("2. View Cases");
            System.out.println("3. Find Case By ID");
            System.out.println("4. Upload Evidence");
            System.out.println("5. View Evidence");
            System.out.println("6. Find Evidence By ID");
            System.out.println("7. Delete Case");
            System.out.println("8. Delete Evidence");
            System.out.println("9.Verify Evidence Integrity");
            System.out.println("10. Logout");

            System.out.print(
                    "Enter your choice: "
            );

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    createCase(
                            scanner,
                            caseService,
                            loggedInUser
                    );

                    break;

                case 2:

                    displayCases(caseDAO);

                    break;

                case 3:

                    System.out.print(
                            "Enter Case ID: "
                    );

                    int caseId =
                            readInt(scanner);

                    try {

                        Case caseObject =
                                caseService.getCaseById(
                                        caseId
                                );

                        System.out.println(
                                caseObject
                        );

                    } catch (
                            CaseNotFoundException e) {

                        System.out.println(
                                e.getMessage()
                        );
                    }

                    break;

                case 4:

                    uploadEvidence(
                            scanner,
                            caseService,
                            evidenceService,
                            loggedInUser
                    );

                    break;

                case 5:

                    displayEvidence(evidenceDAO);

                    break;

                case 6:

                    System.out.print(
                            "Enter Evidence ID: "
                    );

                    int evidenceId =
                            readInt(scanner);

                    try {

                        Evidence evidence =
                                evidenceService
                                        .getEvidenceById(
                                                evidenceId
                                        );

                        System.out.println(
                                evidence
                        );

                    } catch (
                            EvidenceNotFoundException e) {

                        System.out.println(
                                e.getMessage()
                        );
                    }

                    break;

                case 7:

                    System.out.print(
                            "Enter Case ID to delete: "
                    );

                    int deleteCaseId =
                            readInt(scanner);

                    try {

                        caseService.deleteCase(
                                deleteCaseId
                        );

                    } catch (
                            CaseNotFoundException e) {

                        System.out.println(
                                e.getMessage()
                        );
                    }

                    break;

                case 8:

                    System.out.print(
                            "Enter Evidence ID to delete: "
                    );

                    int deleteEvidenceId =
                            readInt(scanner);

                    try {

                        evidenceService.deleteEvidence(
                                deleteEvidenceId
                        );

                    } catch (
                            EvidenceNotFoundException e) {

                        System.out.println(
                                e.getMessage()
                        );
                    }

                    break;
                    
                case 9:

                    System.out.print(
                            "Enter Evidence ID to verify: "
                    );

                    int verifyEvidenceId =
                            readInt(scanner);

                    try {

                        boolean valid =
                                evidenceService.verifyEvidence(
                                        verifyEvidenceId
                                );

                        if (valid) {

                            System.out.println();
                            System.out.println(
                                    "Evidence integrity verified."
                            );

                            System.out.println(
                                    "The file has not been modified."
                            );

                        } else {

                            System.out.println();
                            System.out.println(
                                    "Evidence integrity verification FAILED."
                            );

                            System.out.println(
                                    "The file may have been modified."
                            );
                        }

                    } catch (
                            EvidenceNotFoundException e) {

                        System.out.println(
                                e.getMessage()
                        );
                    }

                    break;

                case 10:

                    loggedIn = false;

                    System.out.println(
                            "Logged out successfully."
                    );

                    break;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    // =====================================================
    // AUDITOR MENU
    // =====================================================

    private static void auditorMenu(
            Scanner scanner,
            CaseDAO caseDAO,
            EvidenceDAO evidenceDAO,
            AuditDAO auditDAO) {

        boolean loggedIn = true;

        while (loggedIn) {

            System.out.println();
            System.out.println(
                    "========== AUDITOR MENU =========="
            );

            System.out.println("1. View Cases");
            System.out.println("2. View Evidence");
            System.out.println("3. View Audit Logs");
            System.out.println("4. Logout");

            System.out.print(
                    "Enter your choice: "
            );

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    displayCases(caseDAO);

                    break;

                case 2:

                    displayEvidence(evidenceDAO);

                    break;

                case 3:

                    displayAuditLogs(auditDAO);

                    break;

                case 4:

                    loggedIn = false;

                    System.out.println(
                            "Logged out successfully."
                    );

                    break;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    // =====================================================
    // CREATE CASE
    // =====================================================

    private static void createCase(
            Scanner scanner,
            CaseService caseService,
            User loggedInUser) {

        System.out.print(
                "Enter Case ID: "
        );

        int caseId =
                readInt(scanner);

        System.out.print(
                "Enter Case Title: "
        );

        String title =
                scanner.nextLine();

        System.out.print(
                "Enter Description: "
        );

        String description =
                scanner.nextLine();

        Case caseObject = new Case(
                caseId,
                title,
                description,
                loggedInUser,
                LocalDate.now(),
                CaseStatus.OPEN
        );

        try {

            caseService.createCase(
                    caseObject
            );

        } catch (DuplicateCaseException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =====================================================
    // UPLOAD EVIDENCE
    // =====================================================

    private static void uploadEvidence(
            Scanner scanner,
            CaseService caseService,
            EvidenceService evidenceService,
            User loggedInUser) {

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
        System.out.println(
                "Select Evidence Type:"
        );

        System.out.println("1. IMAGE");
        System.out.println("2. VIDEO");
        System.out.println("3. AUDIO");
        System.out.println("4. DOCUMENT");
        System.out.println("5. PDF");
        System.out.println("6. TEXT");
        System.out.println("7. OTHER");

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

            default:
                evidenceType = EvidenceType.OTHER;
        }

        System.out.print(
                "Enter File Path: "
        );

        String filePath =
                scanner.nextLine();

        System.out.print(
                "Enter Case ID: "
        );

        int caseId =
                readInt(scanner);

        try {

            Case caseObject =
                    caseService.getCaseById(
                            caseId
                    );

            Evidence evidence =
                    new Evidence(
                            evidenceId,
                            evidenceName,
                            evidenceType,
                            filePath,
                            loggedInUser,
                            LocalDateTime.now(),
                            caseObject,
                            "NOT_GENERATED"
                    );

            evidenceService.uploadEvidence(
                    evidence
            );

        } catch (
                CaseNotFoundException e) {

            System.out.println(
                    e.getMessage()
            );

        } catch (
                DuplicateEvidenceException e) {

            System.out.println(
                    e.getMessage()
            );
        }
    }

    // =====================================================
    // DISPLAY CASES
    // =====================================================

    private static void displayCases(
            CaseDAO caseDAO) {

        System.out.println();
        System.out.println(
                "====== ALL CASES ======"
        );

        if (caseDAO.getAll().isEmpty()) {

            System.out.println(
                    "No cases available."
            );

        } else {

            for (Case caseObject :
                    caseDAO.getAll()) {

                System.out.println(
                        caseObject
                );
            }
        }
    }

    // =====================================================
    // DISPLAY EVIDENCE
    // =====================================================

    private static void displayEvidence(
            EvidenceDAO evidenceDAO) {

        System.out.println();
        System.out.println(
                "====== ALL EVIDENCE ======"
        );

        if (evidenceDAO.getAll().isEmpty()) {

            System.out.println(
                    "No evidence available."
            );

        } else {

            for (Evidence evidence :
                    evidenceDAO.getAll()) {

                System.out.println(
                        evidence
                );
            }
        }
    }

    // =====================================================
    // DISPLAY AUDIT LOGS
    // =====================================================

    private static void displayAuditLogs(
            AuditDAO auditDAO) {

        System.out.println();
        System.out.println(
                "====== AUDIT LOGS ======"
        );

        if (auditDAO.getAll().isEmpty()) {

            System.out.println(
                    "No audit logs available."
            );

        } else {

            for (AuditLog log :
                    auditDAO.getAll()) {

                System.out.println(log);
            }
        }
    }

    // =====================================================
    // READ INTEGER
    // =====================================================

    private static int readInt(
            Scanner scanner) {

        while (true) {

            try {

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.print(
                        "Please enter a valid number: "
                );
            }
        }
    }
}