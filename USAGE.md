## 🧭 Utilizzo di base
### 🧩 Primo avvio

Al primo avvio, se il **Keystore PKCS#12** dei certificati fidati non è presente (o se l’opzione PKCS#11 non è abilitata e il keystore personale non esiste), viene automaticamente mostrata la finestra di configurazione iniziale.

In questa finestra è possibile:
- scegliere i percorsi in cui creare e salvare i keystore;
- generare un certificato digitale *self-signed* con chiavi **NIST P-384**.

---

### 🔐 Cifratura (Encrypt)

1. Fai clic sul pulsante **Encrypt** e seleziona il file da cifrare.
2. Seleziona il destinatario: il menu a tendina mostra i certificati presenti nel **Keystore personale** e in quello dei **certificati fidati**.
   Puoi anche aprire la scheda **File** per importare un certificato esterno.
3. *(Opzionale)* L’algoritmo e l’encoding sono selezionati automaticamente in base alle impostazioni predefinite, ma puoi modificarli per questa singola operazione.
4. Premi **Encrypt** e attendi la conferma.

---

### 🔓 Decifratura (Decrypt)

1. Fai clic su **Decrypt** e seleziona il file da decifrare (verranno mostrati solo i file con estensione `.p7e`).
2. Se il software individua automaticamente la chiave privata corretta, questa verrà selezionata nel menu a tendina. In caso contrario, puoi sceglierla manualmente.
3. Premi **Decrypt**, inserisci la password della chiave privata e attendi la conferma.

---

### 🖋️ Firma (Sign)

> [!WARNING]
> Le firme digitali utilizzano lo standard **CAdES** (*CMS Advanced Electronic Signature*).
> Tuttavia, il software **non è destinato all’apposizione o alla verifica di firme elettroniche qualificate**.
> L’uso è puramente tecnico e sperimentale.

1. Fai clic su **Sign** e seleziona il file da firmare.
2. Seleziona il certificato da utilizzare per la firma.
3. *(Opzionale)* L’algoritmo di hashing e il formato di codifica vengono impostati automaticamente; puoi comunque modificarli per questo caso specifico.
4. *(Opzionale)* Per impostazione predefinita la firma è di tipo **Enveloping**, cioè il file `.p7m` conterrà sia il contenuto che la firma.
   Se desideri una firma **Detached**, seleziona l’apposita opzione.
5. Premi **Sign**, inserisci la password della chiave privata e attendi la conferma.

---

### ✔️ Verifica (Verify)

1. Fai clic su **Verify** e seleziona il file da verificare (verranno mostrati solo file con estensione `.p7m` o `.p7s`).

   * In caso di file `.p7s` (firma *Detached*), verrà mostrato un secondo dialogo per selezionare il contenuto da verificare.
2. Attendi il completamento della verifica e consulta l’esito fornito dal software.
   Per esportare il contenuto (solo per firme *Enveloping*), premi **Export Content**.



## 🧱 PKCS#11

L’applicativo supporta dispositivi conformi allo standard **PKCS#11**.
Per utilizzarli è necessario predisporre un file di configurazione conforme alle specifiche *SunPKCS11*.

### 🔧 Esempio di configurazione (Windows con OpenSC)

```text
name = OpenSC
library = C:\Program Files\OpenSC Project\OpenSC\pkcs11\opensc-pkcs11.dll
slotListIndex = 0
```

> [!WARNING]
> Il software **non supporta** la creazione o l’eliminazione di coppie di chiavi sui dispositivi PKCS#11.