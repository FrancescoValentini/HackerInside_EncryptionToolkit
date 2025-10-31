## 🧭 Basic Usage

### 🧩 First Launch

On first startup, if the **PKCS#12 keystore** of trusted certificates is not present (or if the *PKCS#11* option is disabled and the personal keystore does not exist), the initial configuration window is automatically displayed.

In this window, you can:

* choose where to create and save the keystores;
* generate a *self-signed* digital certificate with **NIST P-384** keys.

---

### 🔐 Encryption (Encrypt)

1. Click **Encrypt** and select the file to encrypt.
2. Choose the recipient: the dropdown menu lists certificates from both the **personal keystore** and the **trusted certificates keystore**.
   You can also open the **File** tab to import an external certificate.
3. Click **+** to add the recipient to the list
4. *(Optional)* The algorithm and encoding are automatically selected based on the default settings, but you can change them for this specific operation.
5. Click **Encrypt** and wait for confirmation.

---

### 🔓 Decryption (Decrypt)

1. Click **Decrypt** and select the file to decrypt (only files with the `.p7e` extension will be shown).
2. If the software automatically detects the correct private key, it will be pre-selected in the dropdown menu. Otherwise, you can choose it manually.
3. Click **Decrypt**, enter the private key password, and wait for confirmation.

---

### 🖋️ Signing (Sign)

> [!WARNING]
> Digital signatures use the **CAdES** (*CMS Advanced Electronic Signature*) standard.
> However, the software **is not intended for creating or verifying qualified electronic signatures**.
> Its use is purely technical and experimental.

1. Click **Sign** and select the file to sign.
2. Choose the certificate to use for signing.
3. *(Optional)* The hashing algorithm and encoding format are automatically set, but you can change them for this particular operation.
4. *(Optional)* By default, the signature type is **Enveloping**, meaning the resulting `.p7m` file will contain both the content and the signature.
   If you want a **Detached** signature instead, select the corresponding option.
5. Click **Sign**, enter the private key password, and wait for confirmation.

---

### ✔️ Verification (Verify)

1. Click **Verify** and select the file to verify (only files with `.p7m` or `.p7s` extensions will be shown).

   * For `.p7s` files (*Detached* signatures), a second dialog will appear to select the content to verify.
2. Wait for the verification process to complete and review the result displayed by the software.
   To export the content (only for *Enveloping* signatures), click **Export Content**.


## 🧱 PKCS#11

The application supports devices compliant with the **PKCS#11** standard.
To use them, a configuration file following the *SunPKCS11* specification is required.

### 🔧 Example Configuration (Windows with OpenSC)

```text
name = OpenSC
library = C:\Program Files\OpenSC Project\OpenSC\pkcs11\opensc-pkcs11.dll
slotListIndex = 0
```

> [!WARNING]
> The software **does not support** creating or deleting key pairs on PKCS#11 devices.