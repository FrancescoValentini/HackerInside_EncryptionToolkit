## 🧭 Utilizzo di base
### 🧩 Primo avvio

Al primo avvio, se il **Keystore PKCS#12** dei certificati fidati non è presente (o se l'opzione PKCS#11 non è abilitata e il keystore personale non esiste), viene automaticamente mostrata la finestra di configurazione iniziale.

In questa finestra è possibile:
- scegliere i percorsi in cui creare e salvare i keystore;
- **Opzionale:** scegliere gli algoritmi di cifratura e di hashing desiderati
- generare un certificato digitale *self-signed* con chiavi **NIST P-384**.

>[!NOTE]
> **Scelta degli algoritmi:** per default il software sceglie `AES-256-CBC` e `SHA-256` tuttavia sono possibili configurazioni alternative:
> 
> **Massima sicurezza**
> - **Cifratura:** `AES-256-GCM` o `ChaCha20-Poly1305`
> - **Hash:** `SHA-384` o `SHA-512`
> - **Chiavi asimmetriche:** `NIST P-384`, `NIST P-521` o algoritmi PQC
>
> **Maggiore compatibilità**
> - **Cifratura:** `AES-128-CBC` o `AES-256-CBC`
> - **Hash:** `SHA-256`
> - **Chiavi asimmetriche:** `RSA-3072` o `RSA-8192`

---

### 🔐 Cifratura (Encrypt)

1. Fai clic sul pulsante **Encrypt** e seleziona il file da cifrare.
2. Seleziona il destinatario: il menu a tendina mostra i certificati presenti nel **Keystore personale** e in quello dei **certificati fidati**.
   Puoi anche aprire la scheda **File** per importare un certificato esterno o (se presente) puoi utilizzare una chiave simmetrica.
3. Aggiungere il destinatario alla lista con il pulsante **+**
4. *(Opzionale)* L'algoritmo e l'encoding sono selezionati automaticamente in base alle impostazioni predefinite, ma puoi modificarli per questa singola operazione.
5. Premi **Encrypt** e attendi la conferma.

---

### 🔓 Decifratura (Decrypt)

1. Fai clic su **Decrypt** e seleziona il file da decifrare (verranno mostrati solo i file con estensione `.p7e`).
2. Se il software individua automaticamente la chiave privata (o della chiave simmetrica) corretta, questa verrà selezionata nel menu a tendina. In caso contrario, puoi sceglierla manualmente.
3. Premi **Decrypt**, inserisci la password della chiave privata (o della chiave simmetrica) e attendi la conferma.

> [!WARNING]
> Quando si utilizza una chiave simmetrica, il suo identificativo (KID) viene calcolato dall'alias nel keystore (tramite SHA-256).
>
> Se l'alias non coincide, la chiave non verrà rilevata automaticamente. 
> 
> I dati restano comunque decifrabili, ma sarà necessario selezionare manualmente la chiave corretta.


---

### 🖋️ Firma (Sign)

> [!WARNING]
> Le firme digitali utilizzano lo standard **CAdES** (*CMS Advanced Electronic Signature*).
> Tuttavia, il software **non è destinato all'apposizione o alla verifica di firme elettroniche qualificate**.
> L'uso è puramente tecnico e sperimentale.

1. Fai clic su **Sign** e seleziona il file da firmare.
2. Seleziona il certificato da utilizzare per la firma.
3. *(Opzionale)* L'algoritmo di hashing e il formato di codifica vengono impostati automaticamente; puoi comunque modificarli per questo caso specifico.
4. *(Opzionale)* Per impostazione predefinita la firma è di tipo **Enveloping**, cioè il file `.p7m` conterrà sia il contenuto che la firma.
   Se desideri una firma **Detached**, seleziona l'apposita opzione.
5. Premi **Sign**, inserisci la password della chiave privata e attendi la conferma.

---

### ✔️ Verifica (Verify)

1. Fai clic su **Verify** e seleziona il file da verificare (verranno mostrati solo file con estensione `.p7m` o `.p7s`).

   * In caso di file `.p7s` (firma *Detached*), verrà mostrato un secondo dialogo per selezionare il contenuto da verificare.
2. Attendi il completamento della verifica e consulta l'esito fornito dal software.
   Per esportare il contenuto (solo per firme *Enveloping*), premi **Export Content**.

## 🧱 PKCS#11

L'applicativo supporta dispositivi conformi allo standard **PKCS#11**.
Per utilizzarli è necessario predisporre un file di configurazione conforme alle specifiche *SunPKCS11*.
> [!NOTE]
> La configurazione PKCS#11 potrebbe variare in base al proprio sistema operativo, driver PKCS#11 e token.
>
> **Per maggiori informazioni sulla configurazione di PKCS#11:** [SunPKCS11 Reference Guide](https://docs.oracle.com/en/java/javase/24/security/pkcs11-reference-guide1.html)
### 🔧 Esempio di configurazione (Windows con OpenSC)

```text
name = OpenSC
library = C:\Program Files\OpenSC Project\OpenSC\pkcs11\opensc-pkcs11.dll
slotListIndex = 0
allowLegacy=true
showInfo=true
```

### ⚠️ Limitazioni
> [!WARNING]
> Il software **non supporta** la creazione o la cancellazione di coppie di chiavi sui dispositivi PKCS#11

> [!WARNING]
>Quando si utilizza un token PKCS#11, per impostazione predefinita il software disabilita le operazioni di cifratura e decifratura ("encrypt" e "decrypt"). 
> 
> È possibile abilitarle nelle impostazioni togliendo la spunta su:
> - "PKCS#11 sign-only mode"
> - "Use RSA OAEP" nella sezione "Algorithms"

> [!WARNING]
> In modalità PKCS#11, il software non può decifrare contenuti cifrati con RSA OAEP o ECC.

> [!WARNING]
> Il software non limita esplicitamente il supporto per algoritmi PQC quando si utilizza PKCS#11, ma questo caso non è mai stato testato. L'utilizzo di token PKCS#11 PQC potrebbe quindi non funzionare correttamente.