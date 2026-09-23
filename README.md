# Employee Attendance & Payroll Management System

A production-grade, live-deployment-ready modular monolith application for automated monthly Excel attendance parsing, business-rule calculation (late marks, half days, early departures, overtime), salary deduction computation, React dashboard visualization, and OpenPDF salary slip generation.

---

## 1. Project Overview
The **Employee Attendance & Payroll Management System** automates the payroll processing lifecycle for internal HR teams. It ingests standard monthly attendance Excel spreadsheets (such as Keka HR exports), evaluates complex attendance business rules, calculates gross/net salaries and deductions using `BigDecimal` precision, presents metrics in an interactive React dashboard, and generates individual PDF salary slips.

---

## 2. Problem Statement & Objectives
Manual monthly payroll calculations are error-prone and inefficient. HR teams face challenges evaluating flexible reporting windows, multi-threshold late marks, half-day shift cutoffs, early departure instances, and block-based overtime rates. This solution provides a centralized, deterministic engine that accepts raw Excel spreadsheets and automates the entire payroll workflow.

---

## 3. Technology Stack & Versions

### Backend
- **Java**: 21 LTS (`jdk-21.0.12.1`)
- **Framework**: Spring Boot 3.4.3 / 3.5.x
- **Build System**: Apache Maven 3.9.x
- **Excel Parser**: Apache POI 5.4.0 (`poi`, `poi-ooxml`)
- **PDF Generator**: OpenPDF 2.0.3 (`com.github.librepdf:openpdf`)
- **Validation & Logging**: Bean Validation (Jakarta), SLF4J + Logback
- **Testing**: JUnit 5, Mockito 5.x

### Frontend
- **Library**: React 18.3.1 (JavaScript ONLY, no TypeScript)
- **Build Tool**: Vite 6.1.1
- **HTTP Client**: Axios 1.7.9
- **Routing**: React Router DOM 6.28.2
- **Icons**: Lucide React 0.475.0

---

## 4. Architecture & System Flow

Built as a clean **Modular Monolith** without unnecessary microservice overhead:

```
[React 18 Dashboard]
        │
   HTTP / REST API
        ▼
[PayrollController / SalarySlipController]
        │
        ▼
[PayrollService / SalarySlipService]
        │
  ┌─────┴────────────────────────┐
  ▼                              ▼
[ExcelImportService]    [AttendanceService]
  │                              │
  ▼                              ▼
[ExcelAttendanceParser] [AttendanceCalculator]
  │                              │
  └──────────────┬───────────────┘
                 ▼
        [PayrollCalculator]
                 │
                 ▼
     [SalarySlipGenerator (PDF)]
```

### Processing Flow
1. User uploads monthly Excel spreadsheet via drag-and-drop React interface.
2. `ExcelImportService` validates stream and invokes `ExcelAttendanceParser`.
3. `ExcelAttendanceParser` normalizes column headers, extracts date/time decimals (`HH.MM`), and builds domain models.
4. `AttendanceService` passes each daily log to `AttendanceCalculator` to detect late marks, half days, early leaves, and overtime.
5. `PayrollCalculator` applies excess penalties, overtime blocks, computes net payable salary, and updates in-memory summary.
6. React dashboard renders interactive metrics, employee breakdowns, and PDF download triggers.

---

## 5. Attendance & Payroll Business Rules

### Attendance Rules
- **Shift Timings**: Regular shift 09:00 AM to 06:00 PM (9 hours = 540 minutes).
- **Flexibility Window**: 1 hour flexibility between 09:00 AM and 10:00 AM. Punching in within this window requires completing 9 full hours before leaving.
- **Late Marks**:
  - Punch-in after 10:00 AM is a Late Mark.
  - Punch-in between 09:00 AM and 10:00 AM without completing 9 hours is also a Late Mark.
  - 4 Late Marks per month are permitted without penalty.
- **Half Day**:
  - Punch-in after 11:00 AM triggers Half Day.
  - Working $\le$ 4 hours (240 minutes) triggers Half Day.
- **Early Leaving**:
  - For punch-in $\le$ 10:00 AM: leaving before completing 9 hours from punch-in is Early Leaving.
  - For punch-in > 10:00 AM: leaving before 04:00 PM (16:00) is Early Leaving.
  - 2 Early Leaves per month are permitted without penalty.
- **Overtime (OT)**:
  - Minimum 3.5 hours (210 minutes) work beyond regular 9h shift is required to earn OT.
  - Overtime is paid in 3.5-hour blocks (3.5h = 0.5 day pay, 7.0h = 1.0 day pay, 10.5h = 1.5 day pay).

### Salary & Deduction Formulas
- `Daily Salary = Monthly Salary / Applicable Working Days` (default = 22 days)
- `Excess Late Marks = max(0, Late Marks - 4)`
- `Late Deduction = Excess Late Marks * 0.5 * Daily Salary`
- `Half Day Deduction = Half Days * 0.5 * Daily Salary`
- `Excess Early Leaves = max(0, Early Leaves - 2)`
- `Early Leave Deduction = Excess Early Leaves * 0.5 * Daily Salary`
- `Total Deductions = Late Deduction + Half Day Deduction + Early Leave Deduction`
- `OT Pay = OT Blocks * 0.5 * Daily Salary`
- `Final Payable Salary = Monthly Salary + OT Pay - Total Deductions`

---

## 6. Business-Rule Ambiguities vs Implementation Assumptions

| Topic | Distinction | Description / Assumption |
| :--- | :--- | :--- |
| **Daily Salary Basis** | *IMPLEMENTATION ASSUMPTION* | Daily salary is computed as `monthlySalary / applicableWorkingDays` where `applicableWorkingDays` defaults to 22 (configurable via `PayrollPolicy`). |
| **Missing Records** | *IMPLEMENTATION ASSUMPTION* | Missing attendance records are **not** automatically treated as absences; calculations are performed strictly on records provided in the uploaded file. |
| **Penalty Rates** | *IMPLEMENTATION ASSUMPTION* | Each excess late mark (> 4), excess early leave (> 2), and half-day shift deducts 0.5 day's salary. |
| **Sheet Layout** | *IMPLEMENTATION ASSUMPTION* | Auto-detects whether salary data is in a separate sheet (`Salary Data`), embedded, or in standard header format. |

---

## 7. Project Structure

### Backend Structure (`src/main/java/com/example/payroll/`)
```
├── PayrollApplication.java
├── calculator/
│   ├── AttendanceCalculator.java
│   ├── DefaultAttendanceCalculator.java
│   ├── OvertimeCalculator.java
│   ├── DefaultOvertimeCalculator.java
│   ├── PayrollCalculator.java
│   └── DefaultPayrollCalculator.java
├── controller/
│   ├── HealthController.java
│   ├── PayrollController.java
│   └── SalarySlipController.java
├── exception/
│   ├── EmployeeNotFoundException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidExcelException.java
│   └── PayrollCalculationException.java
├── model/
│   ├── AttendanceRecord.java
│   ├── AttendanceResult.java
│   ├── Employee.java
│   ├── PayrollResult.java
│   └── PayrollSummary.java
├── parser/
│   ├── ExcelAttendanceParser.java
│   └── ParsedExcelData.java
├── pdf/
│   └── SalarySlipGenerator.java
├── policy/
│   ├── DefaultPayrollPolicy.java
│   └── PayrollPolicy.java
├── service/
│   ├── AttendanceService.java
│   ├── ExcelImportService.java
│   ├── PayrollService.java
│   └── SalarySlipService.java
└── util/
    └── DateTimeUtils.java
```

### Frontend Structure (`frontend/src/`)
```
├── App.jsx
├── index.css
├── main.jsx
├── components/
│   ├── AttendanceTable.jsx
│   ├── EmployeeDetails.jsx
│   ├── ErrorMessage.jsx
│   ├── FileUpload.jsx
│   ├── LoadingSpinner.jsx
│   ├── Navbar.jsx
│   ├── PayrollTable.jsx
│   ├── SalarySlipButton.jsx
│   └── SummaryCard.jsx
├── hooks/
│   └── usePayroll.js
├── pages/
│   ├── Dashboard.jsx
│   └── EmployeeDetailsPage.jsx
├── services/
│   ├── api.js
│   └── payrollService.js
└── utils/
    ├── formatCurrency.js
    └── formatDate.js
```

---

## 8. REST API Documentation

| Method | Endpoint | Description | Request Body | Response Format |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/payroll/upload` | Upload & process Excel attendance file | `multipart/form-data` (`file`) | `200 OK` + `PayrollSummary` |
| `GET` | `/api/v1/payroll` | Get active calculated payroll metrics | None | `200 OK` + `PayrollSummary` |
| `GET` | `/api/v1/payroll/{employeeId}` | Get payroll details for an employee | Path variable | `200 OK` + `PayrollResult` |
| `GET` | `/api/v1/payroll/{employeeId}/attendance` | Get daily attendance logs for an employee | Path variable | `200 OK` + `List<AttendanceResult>` |
| `GET` | `/api/v1/payroll/{employeeId}/salary-slip` | Download PDF salary slip | Path variable | `200 OK` + `application/pdf` |
| `GET` | `/api/v1/health` | Service health status | None | `200 OK` + JSON status |

---

## 9. How to Build & Run

### Prerequisites
- JDK 21
- Apache Maven 3.9+
- Node.js 18+ & npm

### Running Backend
```bash
# Set Java 21 environment if needed
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.12.1"

# Build project
mvn clean install

# Run backend server (starts on http://localhost:8080)
mvn spring-boot:run
```

### Running Frontend
```bash
cd frontend

# Install dependencies
npm install

# Start Vite development server (starts on http://localhost:3000)
npm run dev

# Or build production bundle
npm run build
```

### Running Automated Tests
```bash
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.12.1"
mvn test
```

---

## 10. Deployment Strategy

### Backend Deployment (Docker / Cloud Hosting)
- Build executable JAR: `mvn clean package`
- Deploy to Railway, Render, AWS Elastic Beanstalk, or Heroku.
- Environment variables: `PORT=8080`.

### Frontend Deployment (Static Hosting)
- Build static production assets: `npm run build` inside `frontend/`.
- Deploy `dist/` directory to Vercel, Netlify, or AWS S3 + CloudFront.
- Configure `VITE_API_BASE_URL` to point to production backend API URL.

---

## 11. Known Limitations & Future Improvements
- **Multi-Tenant Storage**: Current version uses in-memory thread-safe state. Persistence can be integrated using JPA/PostgreSQL.
- **Biometric Integration**: Future releases can accept direct biometric API streams alongside Excel file uploads.
