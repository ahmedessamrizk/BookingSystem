# 🏨 Room Reservation System API

A production-style Spring Boot REST API for managing room reservations with authentication, validation, and conflict handling.

---

## 🔗 Quick Links

* 📚 API Documentation: [https://your-netlify-link.netlify.app](https://booking-system-api-e52f39.netlify.app/)

---

## ERD

<img width="966" height="686" alt="BookingSystemERD" src="https://github.com/user-attachments/assets/fb637a1e-6020-4293-b82b-20a0d670547b" />

---

## 🚀 Features

* 🔐 JWT Authentication & Authorization
* 👤 User management
* 🏢 Room management (CRUD)
* 📅 Reservation system with conflict detection
* ☁️ Image upload & storage using Cloudinary
* ⚠️ Global exception handling
* 📄 OpenAPI (Swagger) documentation

---

## 🧠 Business Logic Highlights

This project focuses on real-world reservation rules:

* A room **cannot be double-booked**
* Reservation must satisfy:

  * `startTime < endTime`
  * must be in the future
* Reservations are:

  * **CONFIRMED** if valid
  * **CANCELLED** if expired (via scheduled job)
* Users can only access their own reservations (unless admin)

---

## ⚡ Challenges & Solutions

### 1. Handling Concurrent Reservations (Race Condition)

**Problem:**
When multiple users attempt to book the same room simultaneously, it can lead to double booking and inconsistent data (race condition).

**Solution:**

* Implemented concurrency control during reservation creation
* Added conflict detection to prevent overlapping bookings
* Ensured validation is executed atomically before confirming reservation

**Result:**

* Prevented double booking
* Ensured data consistency
* Improved system reliability under concurrent requests

---

### 2. Preventing Overlapping Bookings

**Problem:**
Users could create reservations that overlap with existing ones.

**Solution:**

* Implemented time interval validation:

  ```
  newStart < existingEnd && newEnd > existingStart
  ```
* Returned `409 Conflict` when overlap detected

---

### 3. Handling Expired Reservations

**Problem:**
Reservations with past start times remained active.

**Solution:**

* Scheduled job automatically marks expired reservations as **CANCELLED**

---

## 🏗️ Architecture

The project follows a clean layered architecture:

```
Controller → Service → Repository → Database
```

### Key Design Decisions:

* DTO-based API communication (no entity exposure)
* MapStruct for mapping
* Centralized exception handling
* Specification pattern for dynamic queries

---

## 🧰 Tech Stack

* **Backend:** Spring Boot
* **Security:** Spring Security + JWT
* **Database:** PostgreSQL
* **Mapping:** MapStruct
* **Cloud Storage:** Cloudinary
* **API Docs:** OpenAPI (Swagger UI via Netlify)
* **Build Tool:** Maven

