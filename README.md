# Regnskapsassistent
Regnskapsassistent er en JavaFX applikasjon ment for å hjelpe med utgifter som dekkes av andre.

![Bilde av programmet i bruk](https://i.imgur.com/KT6AeC3.png)

## Funksjonalitet
Programmet lar deg laste inn en liste av transaksjoner og gir deg en oversiktlig fargekodet tabell av dine utgifter.
Herfra kan du enkelt markere hvilke avgifter som skal dekkes.

Regnskapsassistent tilbyr en rekke verktøy for å hjelpe deg:

- **Oversiktlig fargekoding.** Hver transaksjon blir får en av fire farger:
  - **Rød:** Transaksjonen dekkes ikke.
  - **Gul:** Transaksjonen blir delvis dekket.
  - **Grønn:** Transaksjonen blir dekket fullstendig.
  - **Blå:** Transaksjonen er inngående (penger gikk inn på konto, ikke ut).

- **Velge akkurat hvor mye av et beløp som skal dekkes.**
Dersom du har en transaksjon der deler av (men ikke hele) beløpet dekkes kan du notere dette nøyaktig.

- **Gruppere transaksjoner i kategorier.**
Programmet lar deg opprette kategorier for å gjøre regnskapet mer oversiktlig.

- **Skriv ned kommentarer for ulike transaksjoner.**
Programmet lar deg skrive en kommentar for å hjelpe deg med å huske hva en transaksjon var for.

- **Lagre arbeidet ditt.**
Programmet lar deg lagre arbeidet og komme tilbake til det senere når du har tid.

- **Eksporter arbeidet ditt.**
Programmet lager en pent formatert tekstfil som summerer opp alt du har gjort.
Transaksjonene blir gruppert etter kategori, og kun transaksjoner der hele eller deler av beløpet dekkes blir vist.
Sum for hver kategori blir også vist, i tillegg til en totalsum for alle transaksjonene på bunnen av dokumentet.

## Hvordan kjøre
Programmet krever Maven og Java 17. Last ned koden og start en terminal i mappen. Skriv så:
`mvn clean javafx:run`

## Hvordan bruke
Når du har åpnet programmet, gå øverst i venstre hjørne og trykk *Fil -> Åpne transaksjonslogg*. Finn så filen du vil laste opp.

### Filformat
Filen må være på følgende format: `"Dato";"Forklaring";"Rentedato";"Ut fra konto";"Inn på konto"`.
Det er vanlig at denne linjen står øverst i fila, så programmet hopper over denne. 
**NB: Pass på at første linje ikke inneholder en transaksjon, hvis ikke blir ikke denne tatt med!**
Alle andre linjer må følge formatet gitt. Datoformatet er på formen `DD.MM.YYYY`.
PS: Feltet "Rentedato" brukes ikke. Dersom du ikke har data for dette feltet fra før kan du bare la det stå tomt, men sørg for at semikolonet blir der det er. (Formatet blir da: `"Dato";"Forklaring";;"Ut fra konto";"Inn på konto"`)

### Behandling av transaksjoner
Transaksjonene skal nå være lastet inn og vises i tabellen, og du kan begynne å jobbe. Du kan navigere mellom transaksjonene enten ved å klikke i tabellen eller ved å bruke de blå opp og ned knappene. Knappene hopper over inngående transaksjoner. Velg en transaksjon for å behandle den (som standard lastes den første inn først).

Trykk på den røde knappen med transaksjonsikonet for å markere en transaksjon som "dekket". Knappen skal da bli grønn. Hvis bare deler av beløpet skal dekkes kan du skrive i feltet under "Dekket beløp". Knappen skal da bli gul.

For å legge transaksjonen i en kategori må du først opprette en. Trykk på pluss-knappen under kategori. Dette skal åpne et vindu der du kan legge til å fjerne kategorier. Dersom du vil endre navnet på en kategori kan du dobbeltklikke på den i denne menyen. Skriv det nye navnet og trykk Enter på tastaturet. For å fjerne en kategori kan du trykke på en kategori og så på "Fjern"-knappen. Lukk vinduet når du er ferdig. Når en kategori er lagt til skal det være mulig å klikke på rullgardinmenyen under "Kategori". Trykk på kategorien du ønsker.

For å legge til en kommentar kan du skrive i tekstfeltet under "Kommentar".

### Lagring
For å lagre det du har gjort så langt kan du trykke på *Fil -> Lagre* eller *Fil -> Lagre som*. For å åpne et lagret arbeid kan du trykke på *Fil -> Åpne arbeid*.

### Eksportering
Når du er ferdig med arbeidet kan du trykke på *Fil -> Eksporter* for å lage et tekstdokument som oppsummerer alt som skal dekkes.

### Avslutte
For å lukke programmet kan du trykke på krysset øverst i høyre hjørne eller på *Fil -> Avslutt*. Pass på at du har lagret alt du ikke vil miste før du gjør dette.

## Credits
Transaksjon ikon laget av//

Transaction icon made by

https://pixsector.com/user/adminUser

https://pixsector.com/icon/free-currency-icon-png-vector/899
