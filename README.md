

<img src="docs/assets/logo.png" alt="Orbit Logo" width="600"/>

---

## Overview

A passenger just sat through a 47-minute flight delay on a rocket headed to low-Earth orbit. They're not happy. Their NPS score is heading in the wrong direction. Your advisor opens their dashboard — now what?

**Enter Orbit** — the advisor dashboard that turns customer catastrophes into loyalty gold.

Built for the rocket travel empire of *Elon Bezos*, Orbit is the all-seeing, all-knowing co-pilot for your customer advisors. Flight delay? Detected. Bad feedback incoming? Already flagged. Customer priority? Calculated to fourteen decimal places. The perfect recovery action — complete with a pre-written, silky-smooth apology email? Served on a silver platter before your advisor even finishes their coffee.

**The mission:** improve your NPS. Unhappy customers can be turned around with the right intervention at the right time — and Orbit is here to spot the problems, score them, and hand your team a solid playbook to make it happen.

---

## ✨ Key Features

| Feature | Description |
|---|---|
| 🛸 **Passenger Lifecycle Management** | End-to-end tracking from lead to returning space traveller |
| 📋 **Smart Onboarding Workflows** | Multi-stage onboarding with automated checklists and compliance validation |
| ⚠️ **Incident & Delay Management** | Real-time incident logging, delay notifications, and action tracking |
| 🤖 **AI-Powered Recommendations** | Intelligent suggestions for flights, hotels, food, and onboarding paths |
| 📊 **NPS & Feedback Engine** | Structured post-flight feedback collection and Net Promoter Score analytics |
| 🎯 **Priority & Advisor System** | Rule-based passenger prioritization and advisor assignment |
| 🔁 **Simulation Engine** | Built-in scenario simulation for stress-testing workflows before deployment |
| 🖥️ **Desktop UI** | Fully featured JavaFX desktop application for operations teams |

---

## 🗂️ Documentation

All documentation lives in the [`docs/`](docs/) folder. Here's where to find what you need:

| Document | Description                                                                                                           | Link |
|---|-----------------------------------------------------------------------------------------------------------------------|---|
| 📘 Technical Documentation | Architecture, class diagrams, sequence diagrams, database schema (ER diagram), incident management & priority scoring | [`docs/TechnicalDocumentation_ATdIT_Group4.pdf`](docs/technical.pdf) |
| 📗 User Guide (End User) | How to use Orbit — customer monitoring, incidents, incident response, customer journeys, flight details               | [`docs/EndUserDoku_Orbit_ATdIT.pdf`](docs/user-guide.pdf) |
| 📙 BPMN Processes | Business process models for onboarding, incidents, and flight operations                                              | [`docs/bpmn.pdf`](docs/bpmn.pdf) |
| 📐 Mockups & UI Design | Screen designs for advisor and CSO dashboards, incident workflows, customer journeys, email templates, logo & branding         | [`docs/mockups.pdf`](docs/mockups.pdf) |

---

## 🏗️ Project Structure

```
src/main/java/
├── config/         # Configures suggestion strategy wiring
├── database/       # Connection & schema initialization
├── model/          # Domain entities, enums, workflow models
├── repository/     # Data access layer
├── service/        # Business logic
├── simulation/     # Scenario simulation engine
├── support/        # Encodes/decodes recovery action notes
└── ui/             # JavaFX desktop application

```

> For a deep dive into architecture decisions and module responsibilities, see the [Technical Documentation](docs/technical.pdf).

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+

### Run

```bash
# Clone the repository
git clone https://github.com/your-org/orbit.git
cd orbit

# Download dependencies
mvn clean install
```

Then simply execute the `Main` class from your IDE or terminal.
