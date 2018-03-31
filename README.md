# IDK0051-eksam-Paikesesysteem
IDK0051 eksamitöö „Päikesesüsteemi postkontor” Sügis 2017 A


# Ülesanne
Päikesesüsteemi postkontor teenindab kõiki Päikesesüsteemi planeete, lisaks Maa Kuud, Jupiteri kuud Io ja kääbusplaneeti Pluuto.
Posti veetakse spetsiaalsete postirakettidega. Päikese peal on liiga kuum – sinna posti ei viida.

## Baasosa(Kõik implementeeritud)
1. Igal teenindataval planeedil ja kuul on postipunkt, kus saab:
* pakke raketti laadida
* pakke raketist maha laadida
* rakketti teenindada, mille käigus tangitakse raketi kütusepaak täis
  * Erisus täispunktide saamiseks (baasversioonis pole vaja): Jupiteril ja Neptuunil asuvates teeninduspunktides vahetatakse teeninduse käigus lisaks ka raketi kosmilise kiirguse andur kui selle ressurss on 2 starti või vähem

2. Rakett kasutab kütusena tumeainet, mida talle teeninduse ajal tangitakse. Tumeaine paagi suurus liitrites ei ole teada, kuid on teada, et
* 100% täis paagiga saab rakett teha 5 starti (iga start kulutab 20% kütust)
* võib mõelda nii, et 100% täis paak sisaldab 100 ühikut kütust
* iga 5 tankimise järel peab raketti teenindama (kuid loomulikult võib teenindada sagedamini)
* tähtis on, et rakett peaks arvet, mitu ühikut talle kokku kütust on tangitud ja mis on tema hetkeline kütusevaru

3. Merkuurile ja Veenusele on suutelised sõitma vaid erilist tüüpi kuumakindlad raketid
* selliste rakettide kütusekulu igal stardil on 25% mahust
* start Merkuurilt ja Veenuselt kulutab aga lausa utoopilised 50% kütusest

4. Raketi kandevõime
* Rakett suudab korraga peale võtta 100 kg pakke
  * Spetsiaalne kuumakindel rakett tänu oma raskusele vaid 80 kg pakke

###### Pakk
5. Igal transporditaval pakil on:
* lähtepunkt
* sihtpunkt
* kaal (vahemikus 1 – 80 kg)

###### Postipunktid
6. Iga postipunkt peab arvestust vastuvõetud pakkide kohta. Igal ajahetkel saab pärida:
* mitu pakki on vastu võetud
* mis on nende kogukaal
* mis on nende keskmine kaal
* planeetide nimekiri, kust siia pakke on saadetud
* küsija saab pärida oma kriteeriumitele vastavate pakkide arvu, näited:
  * Mitu pakki on saadetud Pluutolt?
  * Mitu üle 7kg pakki on saadetud?
  * Mitu alla 60 kg pakki on saadetud Veenuselt?

7. Lend ühelt planeedilt teisele
* Kaine loogika vastaselt võtavad kõik lennud (teie õnneks) ühesuguse 15 ms, sõltumata läbitud vahemaast

8. Süsteemi käivitamine ja reeglid
* Mõistagi peavad kõik raketid töötama samaaegselt
* Korraga teenindatakse ühes postipunktis ühte raketti, teised peavad ootama kui samal ajal sinna satuvad
* Ükski rakett ei stardi tühjalt – alati pakiga. Erisuse võib täisversiooni realiseerimisel teha kui on vaja lennata teenindusjaama, kus saab vahetada kosmilise kiirguse andurit – samas võib sinna lennata ka täislastiga ning viimastel teenindusringidel
* Tavarakett ei tohi peale võtta pakke, mis on mõeldud saatmiseks Merkuurile või Veenusele
  * looge süsteemi käivitamisel 20 tavalist raketti
* Kuumakindel rakett võtab peale vaid pakke, mille lähte- või sihtpunkt on Merkuur või Veenus
  * looge süsteemi käivitamisel 5 kuumakindlat raketti
* Looge eraldi lõim, mis pakke looks. Igale pakile määrab ta juhuvalikuga:
  * lähtekoha
  * sihtkoha
  * kaalu
  * ... ja asetab paki lähtekoha postipunkti ootejärjekorda.
  * Iga paki loomise vahel on 3 ms, kokku luuakse 1500 pakki
  
 ## Täisversioon(Kõik implementeeritud)
 9. Raketi kosmilise kiirguse andur on kallis seade ja peab vastu vaid 25 starti. Peale seda tuleb see vahetada.
* Kosmilise kiirguse andurit ei vahetata kunagi enne kui jäänud on 2 starti või vähem, sest see on kallis
* Kosmilise kiirguse andurit saab vahetada ainult spetsiaalset võimekust omavates teeninduspunktides Jupiteril ja Neptuunil.

10. Nõudmised logistikale:
* Üks rakett võtab korraga peale oma kandevõime ulatuses pakke (st võimalusel mitu pakki)
* Üks rakett saab korraga peale võtta mitmele planeedile mõeldud pakid
  * seetõttu ei toimu postijaamas alati täielikku tühjendamist, vaid maha laetakse vaid sinna mõeldud pakid
* Rakett võtab pakke peale ka siis kui tal mõned juba on peal, aga mahuks veel
  * sel juhul ta eelistab selliseid pakke, mis saadetakse samale planeedile kui olemasolevad
  * Näide: rakett võtab Io pealt peale kaks pakki, mis tuleb viia Maale ja ühe, mis tuleb viia Marsile. Ta viib ühe paki Marsile ära. Kuna nüüd tekkis raketis ruumi, võtab ta Marsilt peale uue paki. Kui Marsil on mõni pakk, mis on mõeldud saatmiseks Maale, eelistab ta seda, sest sinna suundub ta niikuinii järgmiseks. Kui selliseid pakke ei ole, võtab mõne teise, mis lisab raketile uue sihtkoha. Rakett jätkab sõitu Maale.
* Kuivõrd kõikide lendude pikkus on sama (15ms), siis ei pea planeete teenindama nende asukoha järgi Päikesesüsteemis (st võib viia Io pealt paki Maale ja siis Marsile).

11. Töö lõpetamine
* Alguses ei teki ilmselt probleeme sellega, et kuskil pakke ei jaguks – 1000 pakki on piisav, et see planeetide vahel üsna hästi ära jaguneks. Samas töö lõpus võib juhtuda (kuna meil on nõue, et rakett ei stardi tühjalt), et mõnel planeedil on veel kohaletoimetamata pakke, kuid raketid ootavad teistel planeetidel, kus pakke ei ole.
* Seetõttu (võttes kasutusele olemasoleva raketi või luues uue) korraldage postiring – vähemalt üks rakett sõidab järjest läbi kõik planeedid seni, kuni kõik pakid on kohale veetud
  * eriti hea, kui kõik olemasolevad raketid seda teeksid – aga see pole nõutud
  * NB! tavaline rakett ei saa endiselt sõita Veenusele ja Merkuurile
