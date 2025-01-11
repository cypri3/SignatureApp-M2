# 🚨 Educational Project Notice 🚨

**This project is for educational purposes only. It is not intended for use in real-world cryptographic applications. Do not use this implementation for securing sensitive data.**

---

## Introduction

This project provides an application to **digitally sign PDF files** and **verify signatures** embedded in the file's metadata. Users can select from multiple signature algorithms and hash functions, all accessible through an intuitive graphical user interface (GUI). The application also includes a PKI to manage and store cryptographic keys.


## Features

- **Sign PDF Files**  
  Sign your files using RSA, DSA, or ECDSA algorithms. Choose from available hash functions (SHA1, SHA-256, or MD5) for signing.
  
- **Verify PDF Signatures**  
  Verify the validity of a signature embedded in a file's metadata. 

- **Integrated PKI**  
  Manage and store cryptographic keys. Each user is assigned a unique ID for key management.

- **User-Friendly GUI**  
  Easily create new users, load files, and manage signature operations through an intuitive interface.


## How It Works

### Signing a File

1. **Create or Load a User**  
   - First-time users: Click “Create a new user” to generate a unique ID.  
   - Returning users: Load your profile to access saved keys.

2. **Load the PDF**  
   - Drag and drop a file into the GUI or click the “Load File” button.

3. **Select Signing Options**  
   - Choose a signature algorithm (RSA, DSA, ECDSA).  
   - Choose a hash function (SHA1, SHA-256, MD5).

4. **Sign the File**  
   - The signature is embedded in the file's metadata.  
   - Re-signing the same file will replace the previous signature.

### Verifying a Signature

1. **Load the PDF**  
   - Drag and drop or click the “Load File” button.

2. **Verify the Signature**  
   - Select the signature algorithm used for signing.  
   - Click the “Verify” button.


## Technical Details

- **Signature Algorithms**  
  RSA, DSA, and ECDSA are implemented using a common interface to ensure consistent methods (`keyGen`, `sign`, `verify`).  
  - RSA and DSA operate in a modular number field.  
  - ECDSA operates on elliptic curve groups.

- **Hash Functions**  
  SHA1, SHA-256, and MD5 are implemented through a shared interface. Users can select the desired hash function for signing.

- **Public Key Infrastructure (PKI)**  
  - Keys are stored in plaintext but encoded in hexadecimal for added obfuscation.  
  - User IDs are used to associate keys with specific users.

- **PDF Metadata Management**  
  Metadata manipulation is handled using the **PDFBox library**, enabling seamless embedding and retrieval of digital signatures.


## Compilation and Execution

### Linux

To compile and run the application on a Linux system:  
- **Compile**:  
  ```bash
  make compile
  ```  
- **Run**:  
  ```bash
  make run
  ```  
- **Clean Build Files**:  
  ```bash
  make clean
  ```  
- **Help**:  
  ```bash
  make help
  ```

### Windows Command Prompt (CMD)

To compile and run the application on Windows using CMD:  
- **Compile**:  
  ```cmd
  build.bat compile
  ```  
- **Run**:  
  ```cmd
  build.bat run
  ```  
- **Clean Build Files**:  
  ```cmd
  build.bat clean
  ```  
- **Help**:  
  ```cmd
  build.bat help
  ```

### Windows PowerShell

To compile and run the application on Windows using PowerShell:  
- **Compile**:  
  ```powershell
  .\build.ps1 compile
  ```  
- **Run**:  
  ```powershell
  .\build.ps1 run
  ```  
- **Clean Build Files**:  
  ```powershell
  .\build.ps1 clean
  ```  
- **Help**:  
  ```powershell
  .\build.ps1 help
  ```


## Requirements

- **Java**  
  Ensure Java is installed on your system.  
- **PDFBox Library**  
  The project uses Apache PDFBox for managing PDF metadata. Ensure it is included in your build.


## Limitations

- Only PDF files are supported.  
- The PKI file is stored in plaintext, though keys are encoded in hexadecimal.