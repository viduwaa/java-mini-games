# Mini Games Platform

A **JavaFX-based Mini Games Platform** featuring multiple games with live leaderboards, user authentication, and score tracking. Built for educational purposes as a university mini-project.

---

## 📌 Table of Contents

* [Project Overview](#project-overview)
* [Features](#features)
* [Tech Stack](#tech-stack)
* [Database Structure](#database-structure)
* [Installation & How to Run](#installation--how-to-run)
* [Usage](#usage)
* [Screenshots](#screenshots)
* [Future Enhancements](#future-enhancements)
* [Author](#author)

---

## 🔹 Project Overview

This project provides a desktop platform for playing mini-games like **Snake** and **Tetris**, tracking user scores, and displaying leaderboards for each game and total scores. The platform emphasizes **interactive gameplay**, **real-time score updates**, and **a clean UI layout**.

---

## 🔹 Features

* User login and session management.
* Play multiple games (Snake & Tetris).
* **Real-time score updates** displayed in the header.
* Game-specific leaderboards (Snake & Tetris) **side by side**.
* Total score leaderboard combining all games.
* Back navigation to main menu.
* SQLite database for storing users and scores.
* Clean and modern UI using JavaFX layouts (`HBox`, `VBox`, `StackPane`).

---

## 🔹 Tech Stack

* **Frontend:** JavaFX
* **Backend:** Java 21
* **Database:** SQLite
* **Design Patterns:** DAO pattern, MVC-like separation
* **Other Libraries:** JavaFX Media for background videos

---

## 🔹 Database Structure

The project uses **three tables** in 3NF:

### Users

| Column   | Type    | Notes                      |
| -------- | ------- | -------------------------- |
| user\_id | INTEGER | Primary key, autoincrement |
| username | TEXT    | Unique, not null           |
| password | TEXT    | Not null                   |

### Games

| Column   | Type    | Notes                      |
| -------- | ------- | -------------------------- |
| game\_id | INTEGER | Primary key, autoincrement |
| name     | TEXT    | Unique, not null           |

### Scores

| Column     | Type      | Notes                         |
| ---------- | --------- | ----------------------------- |
| score\_id  | INTEGER   | Primary key, autoincrement    |
| user\_id   | INTEGER   | Foreign key → users(user\_id) |
| game\_id   | INTEGER   | Foreign key → games(game\_id) |
| score      | INTEGER   | Not null                      |
| played\_at | TIMESTAMP | Defaults to current timestamp |

---

## 🔹 Installation & How to Run

### 1️⃣ Clone the repository

```bash
git clone https://github.com/viduwaa/java-mini-games.git
cd java-mini-games
```

### 2️⃣ Install Maven

#### On Linux (Ubuntu/Debian):

```bash
sudo apt update
sudo apt install maven
```

#### On Windows:

1. Download Apache Maven from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi).
2. Extract the ZIP to a directory (e.g., `C:\Program Files\Apache\Maven`).
3. Add Maven `bin` folder to the **System Environment PATH**. Example: `C:\Program Files\Apache\Maven\bin`.
4. Open a new Command Prompt and check version:

```cmd
mvn -v
```

---

### 3️⃣ Build the Project

From the project root folder, run:

```bash
mvn clean install
```

* This downloads dependencies, compiles the code, and packages the project.

---

### 4️⃣ Run the Project

Run the JavaFX application using Maven:

```bash
mvn javafx:run
```

* Ensure your **JavaFX SDK** is properly configured in the `pom.xml` if required.
* The **Main Menu** will appear with Snake, Tetris, and leaderboard access.


---

### 🔹 Notes

* All resources (images, videos, FXML files) should remain in the `/resources` folder.
* After building with Maven, future runs can be done with `mvn javafx:run` only.

---

## 🔹 Usage

1. **Login or create a new user**.
2. Select a game from the main menu: **Snake** or **Tetris**.
3. Play the game and watch your **score update in real-time** in the header.
4. Click the **Leaderboard button** to see **both Snake and Tetris leaderboards side by side**.
5. Use the **Back button** to return to the main menu.



---

## 🔹 Future Enhancements

* Multiplayer game modes.
* Additional mini-games.
* More advanced leaderboard features (filters, weekly/monthly stats).

---

## 🔹 Author

**Vidula Deneth**
Department of ICT, Rajarata University of Sri Lanka

---

## 🔹 License

This project is for educational purposes. Feel free to modify and learn from it.
