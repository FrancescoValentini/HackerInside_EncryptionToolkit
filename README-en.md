# HackerInside EncryptionToolkit
<p align="center">
<img src="resources/it/hackerinside/etk/GUI/icons/app_icon.png" width="300" height="300"/>
</p>

---

[🇮🇹 Italian Version](README.md)

**HackerInside EncryptionToolkit** is software for encryption and digital signing based on the **CMS (Cryptographic Message Syntax)** standard.
It supports **encryption, decryption, key management, and digital signature** with integration for **PKCS#11** devices.

> [!CAUTION]
> **Keystore Security**
>
> In the case of *PKCS#12 Keystores*, cryptographic keys are protected by two passwords:
>
> - a *master password* for the entire keystore
> - a *specific password* for the individual private key
>
> These passwords are the **only protection mechanism** in case the file is compromised.
> The software **cannot guarantee the security of the keys** if weak passwords are used.
>
> The keystore is protected with the following parameters:
`PBES2, PBKDF2, AES-256-CBC, Iteration 100000, PRF hmacWithSHA256`
>
> For *PKCS#11* devices, security depends on the device itself and the configured PIN.

## ⚙️ Main Features

> [!NOTE]
> **Compatibility**
>
> The software uses the **CMS (Cryptographic Message Syntax)** standard.
> This ensures that data encrypted or signed with this application is compatible with other software conforming to the same standard (for example, **OpenSSL**).
>
> Future versions of the program will not compromise compatibility, since the CMS standard is not altered.
> Any incompatibilities may only arise from support for specific cryptographic algorithms.
>
> **Recommended configurations:**
>
> 🔒 *Maximum Security*
>
> - **Encryption:** `AES-256-GCM` or `ChaCha20-Poly1305`
> - **Hash:** `SHA-384` or `SHA-512`
> - **Asymmetric keys:** `NIST P-384` or `NIST P-512`
>
> 🔄 *Greater Compatibility*
>
> - **Encryption:** `AES-128-CBC` or `AES-256-CBC`
> - **Hash:** `SHA-256`
> - **Asymmetric keys:** `RSA-3072` or `RSA-8192`

### 🔑 Certificate and Key Management
- Import/export of certificates in **PEM** or **DER** format
- Import from **PKCS#12** files (`.p12`, `.pfx`) for key/certificate pairs
- Export in **PEM** or **DER** format
- Integration with **PKCS#11** devices (HSM/token)

### 🔐 Supported Encryption Algorithms
- `AES-128-CBC`, `AES-128-GCM`
- `AES-256-CBC`, `AES-256-GCM`
- `ChaCha20-Poly1305`

### 🧮 Hashing Algorithms
- `SHA-256`
- `SHA-384`
- `SHA-512`

### 📁 Input/Output Formats
- `DER`
- `PEM`

## 🧭 Basic Usage
- [Guide](USAGE-en.md)

## 🧰 Technologies Used

- **Cryptographic library:** [BouncyCastle](https://www.bouncycastle.org/)
- **UI library:** [FlatLaf](https://www.formdev.com/flatlaf/)
- **GUI framework:** Java Swing

## ⚠️ Disclaimer
This application has been developed **for educational and recreational purposes only**.
The author assumes **no responsibility** for any illegal use or loss of information.

Digital signatures use the **CAdES** (*CMS Advanced Electronic Signature*) standard.
However, this software **is not intended for creating or verifying qualified electronic signatures**.
Its use is purely **technical and experimental**.
