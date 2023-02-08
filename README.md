# Energy Networks

## Spis treści
* [Wstęp](#wstep)
* [Wymagania uruchomieniowe](#wymagania)
* [Elementy aplikacji](#elementyaplikacji)
* [Opis uruchomienia](#opisuruchomienia)

<a name="wstep"></a>
## Wstęp
Projekt Energy Networks umożliwia przeprowadzenie eksperymentu wyznaczania cen w systemie elektroenergetycznym dla następujących metod cenotwórstwa:
* metoda cen jednolitych,
* metoda cen węłowych,
* metoda cenotwórstwa LP+.

Projekt zawiera pliki z danymi następujących modeli sieci:
* 5-węzłowa sieć testowa MATPOWER (zmodyfikowana),
* 9-węzłowa sieć testowa MATPOWER (zmodyfikowana),
* 30-węzłowa sieć testowa MATPOWER,
* 39-węzłowa sieć testowa MATPOWER,
* modelowa sieć wysokich napięć KSE.

Obliczenia mogą zostać przeprowadzone dla zapotrzebowania z dnia zimowego (06.12.2022r.) oraz dnia letniego (12.07.2022r.) na podstawie danych PSE (https://www.pse.pl/dane-systemowe/funkcjonowanie-kse/raporty-dobowe-z-pracy-kse/wielkosci-podstawowe) dla modelu sieci KSE oraz odpowiednio przeskalowanego zapotrzebowania w przypadku sieci testowych.


<a name="wymagania"></a>
## Wymagania uruchomieniowe

* Java w wersji 11,
* AMPL API (instrukcja instalacji na stronie: https://ampl.com/products/ampl/apis/).


<a name="elementyaplikacji"></a>
## Elementy aplikacji

Projekt składa się z dwóch główny elementów. Pierwszym z nich jest generator modeli danych dla modelu obliczeniowego AMPL, któy służy do rozwiązywania problemu optymalnego rozpływu mocy w wariancie stałoprądowym (OPF DC). Element ten podzielony jest na dwa składniki:
* parser tworzący modele danych dla AMPL z plików zawierających dane sieci testowych (MATPOWER, IEEE) -- Parser.java,
* parser tworzący modele danych dla modelowej sieci wysokich napięć KSE -- KSEParser.java.

Drugim elementem jest aplikacja wywołująca obliczenia modelu OPF DC za pośrednictwem AMPL API -- Ampl.java. Jest ona również odpowiedzialna za zbieranie, przetwarzanie oraz zapisywanie wyników w odpowiednio zdefioniowanej formie.

<a name="opisuruchomienia"></a>
## Opis uruchomienia