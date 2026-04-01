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

## 🏗️ System Architecture

    Mobile App
        ↓
    OCR Engine (Receipt Parsing)
        ↓
    Extracted Text Data 
        ↓ 
    Text Preprocessing 
        ↓ 
    TF-IDF Vectorizer 
        ↓ 
    Linear SVM Model 
        ↓
    Predicted Category
        ↓
    Mobile App

---

## 📸 Screenshots

### 🔹 Sign In & Sign Up Screen

<p align="left">
  <img src="screenshots/sign_in.png" width="200"/>
  <img src="screenshots/sign_up.png" width="200"/>
</p>

### 🔹 Add Expense Options
<p align="left">
  <img src="screenshots/add_options.png" width="200"/>
</p>

### 🔹 Receipt Scanning (OCR & Category Prediction Result)
<p align="left">
  <img src="screenshots/receipt.jpg" width="200"/>
  <img src="screenshots/scan_result.png" width="200"/>
</p>

### 🔹 Manual Expense Entry
<p align="left">
  <img src="screenshots/manual_input.png" width="200"/>
</p>

### 🔹 Analytics Dashboard
<p align="left">
  <img src="screenshots/analytics_1.png" width="200"/>
  <img src="screenshots/analytics_2.png" width="200"/>
</p>

### 🔹 Budget Tracking
<p align="left">
  <img src="screenshots/budgets.png" width="200"/>
</p>

---

## 🚀 Features

### ✅ Core Features
- 📸 **Receipt Scanning (OCR)**  
  Extracts key information such as total amount, date, and merchant from receipt images.

- 🤖 **AI Expense Categorisation**  
  Automatically classifies expenses the following categories:
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

- 📊 **Analytics Dashboard**  
  Visualises spending patterns using bar charts and pie charts.

- 💰 **Budget Tracking**  
  Monitor spending against budget limits.

- ✍️ **Manual Expense Entry**  
  Allows users to input expenses manually when OCR is unavailable.

---

## 🧠 AI Categorisation Model

- **Input:** OCR-extracted receipt text  
- **Preprocessing:** Text cleaning and normalization  
- **Feature Extraction:** TF-IDF (unigrams + bigrams)  
- **Model:** Linear Support Vector Machine (LinearSVC)  
- **Handling Imbalance:** Class weights  
- **Evaluation:** Classification report and confusion matrix

### Model Pipeline

<img width="300" height="390" alt="mermaid-diagram" src="https://github.com/user-attachments/assets/0755276d-492b-41ff-988c-96fa5bdf9677" />

---

## 🛠️ Tech Stack

### Frontend (Mobile App)
- Android (Kotlin, Jetpack Compose)

### Backend
- **Firebase** (data storage)
- **FastAPI** (for AI categorisation)

### Machine Learning
- **Python**
- **Scikit-learn**
  - TF-IDF Vectorizer
  - Linear SVM
 
### Data Processing
- Pandas
- NumPy

---
