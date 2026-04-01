# 💰 Smart Expense Tracker with OCR & AI Analytics

A mobile application that automates expense tracking by combining **OCR (Optical Character Recognition)** and **AI-powered categorisation**, with integrated analytics and budgeting features.

---

## 📌 Project Overview

Managing personal expenses manually is time-consuming and error-prone. Users often forget to log expenses or miscategorise them, resulting in poor financial awareness.

This project aims to solve that problem by:
- 📸 Scanning receipts using OCR (Optical Character Recognition)
- 🤖 Automatically categorising expenses using AI
- 📊 Providing analytics and insights on spending habits
- 💸 Supporting budgeting to help users manage finances effectively

---

## 🚀 Features

### ✅ Core Features
- **Manual Expense Entry**
- **Receipt Scanning (OCR)**
- **AI Expense Categorisation**
- **Analytics Dashboard**
  - Bar chart: Spending over time
  - Donut chart: Spending by category
- **Budget Tracking**
- **Cloud Storage (Firebase)**

---

## 🧠 AI Categorisation Model

The categorization model is trained using **text data**. It classifies expenses into the following categories:

- Food & Drinks  
- Entertainment  
- Groceries  
- Transport  
- Home  
- Wearables  
- Beauty  
- Healthcare  
- Education
- Others

### Model Pipeline

- Text preprocessing and cleaning  
- TF-IDF vectorization (1–2 grams)  
- Logistic Regression classifier  
- Output probabilities for confidence scoring

### Why this approach?
- Works well with **short, noisy OCR text**
- Efficient and fast for **real-time predictions**
- Provides **confidence scores** for better UX

---

## 🛠️ Tech Stack

### Mobile App
- **Kotlin (Jetpack Compose)**
- Android SDK

### Backend
- **Firebase** (data storage)
- **Cloud API** (for AI categorisation)

### Machine Learning
- **Python**
- **Scikit-learn**
  - TF-IDF Vectorizer
  - Logistic Regression
