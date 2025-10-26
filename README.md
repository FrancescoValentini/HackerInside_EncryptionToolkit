# HackerInside EncryptionToolkit
<p align="center">
<img src="resources/it/hackerinside/etk/GUI/icons/app_icon.png" width="300" height="300"/>
</p>

---
[🇺🇸 English Version](README-en.md)

**HackerInside EncryptionToolkit** è un software per la cifratura e la firma digitale basato sullo standard **CMS (Cryptographic Message Syntax)**.
Supporta operazioni di **cifratura, decifratura, gestione delle chiavi e firme digitali**, con integrazione per dispositivi **PKCS#11**.


> [!CAUTION]
> **Sicurezza dei keystore**
>
> Nel caso di *Keystore PKCS#12*, le chiavi crittografiche sono protette da due password:
>
> - una *master password* per l’intero keystore
> - una *password specifica* per la singola chiave privata
>
> Queste password sono l’unico meccanismo di protezione in caso di compromissione del file.
> Il software **non può garantire la sicurezza delle chiavi** se vengono utilizzate password deboli.
>
> Il keystore è protetto con i seguenti parametri:
`PBES2, PBKDF2, AES-256-CBC, Iteration 100000, PRF hmacWithSHA256`
>
> Nei dispositivi *PKCS#11*, la sicurezza dipende dal dispositivo stesso e dal PIN configurato.

## ⚙️ Caratteristiche principali
> [!NOTE]
> **Compatibilità**
>
> Il software utilizza lo standard **CMS (Cryptographic Message Syntax)**.
> Ciò garantisce che i dati cifrati o firmati con questo applicativo siano compatibili con altri software conformi allo stesso standard (ad esempio, **OpenSSL**).
>
> Versioni future del programma non comprometteranno la compatibilità, poiché lo standard CMS non viene alterato.
> Eventuali incompatibilità possono derivare solo dal supporto di specifici algoritmi crittografici.
>
> **Configurazioni consigliate:**
>
> 🔒 *Massima sicurezza*
>
> - **Cifratura:** `AES-256-GCM` o `ChaCha20-Poly1305`
> - **Hash:** `SHA-384` o `SHA-512`
> - **Chiavi asimmetriche:** `NIST P-384` o `NIST P-521`
>
> 🔄 *Maggiore compatibilità*
>
> - **Cifratura:** `AES-128-CBC` o `AES-256-CBC`
> - **Hash:** `SHA-256`
> - **Chiavi asimmetriche:** `RSA-3072` o `RSA-8192`

### 🔑 Gestione certificati e chiavi
- Import/export di certificati in formato **PEM** o **DER**
- Import da file **PKCS#12** (`.p12`, `.pfx`) per coppie chiave/certificato
- Esportazione in formato **PEM** o **DER**
- Integrazione con dispositivi **PKCS#11** (HSM/token)

### 🔐 Algoritmi di cifratura supportati
- `AES-128-CBC`, `AES-128-GCM`
- `AES-256-CBC`, `AES-256-GCM`
- `ChaCha20-Poly1305`

### 🧮 Algoritmi di hashing
- `SHA-256`
- `SHA-384`
- `SHA-512`

### 📁 Formati di input/output
- `DER`
- `PEM`



## 🧭 Utilizzo di base
- [Guida](USAGE.md)


## 🧰 Tecnologie utilizzate

- **Libreria crittografica:** [BouncyCastle](https://www.bouncycastle.org/)
- **Libreria grafica:** [FlatLaf](https://www.formdev.com/flatlaf/)
- **Framework grafico:** Java Swing

## ⚠️ Disclaimer
Questa applicazione è stata sviluppata a scopo puramente ludico. L’autore non si assume alcuna responsabilità per eventuali usi illeciti o per la perdita di informazioni. 

Le firme digitali utilizzano lo standard **CAdES** (*CMS Advanced Electronic Signature*). Tuttavia, il software **non è destinato all’apposizione o alla verifica di firme elettroniche qualificate**. L’uso è puramente tecnico e sperimentale.
