(ns tabellinator.core
  (:gen-class)
  (:require
   [clojure.string :as str]
   [hiccup.core :refer [html]]
   [cli-matic.core :refer [run-cmd]]
   [say-cheez.core :refer [capture-build-env-to]]))

(def BUILD)
(capture-build-env-to BUILD)


; (H/html [:span {:class "x"} "y"])


(defn style
  "For hiccup - see "
  [s]
  {:style
   (str/join ";" (map #(str (name %) ":" ((keyword %) s)) (keys s)))})

(defn rndIncluding
  [min max]
  (+ min (rand-int (inc (- max min)))))

; ----------------------------------------
; GENERATORI
;
; I generatori ritornanio una tupla
; con l'operaziojne ijn prima posizione
; ed il risultato in seconda.
;
; I generatori sono chiamati con tutti i
; parametri della CLI.
;
; ----------------------------------------

(defn dummy
  [_]
  ["1+1" "1"])

(defn multiplications
  "Moltiplicazioni semplici da tabellina"
  [{:keys [min max]}]

  (let [a (rndIncluding min max)
        b (rndIncluding min max)
        c (* a b)]

    [(str a " x " b)
     (str c)]))

(defn divisioni-semplici
  "Divisioni semplici da tabellina"
  [{:keys [min max]}]

  (let [a (rndIncluding min max)
        b (rndIncluding min max)
        c (* a b)]

    [(str c " : " b)
     (str a)]))


;
; LOGICA
;


(defn take-unique
  "Visto che potrebbero esserci gli stessi valori più volte,
  controllo che non si ripetano."
  [fnToCall nEntries]

  (let [infiniteStream (map (fn [_] (fnToCall)) (repeat :dummy))]
    (loop [initial-cache #{}
           nLoop 0]
      (let [initial (take nEntries infiniteStream)
            cache (apply conj initial-cache initial)
            ;quanti ne mancano
            missing (- nEntries (count cache))]

        (cond
          ; non ne mancano
          (>= 0 missing)
          (take nEntries cache)

          ; ne mancano, ma ho già fatto troppi giri
          (< 5 nLoop)
          (take nEntries cache)

          :else
          (recur cache (inc nLoop)))))))

(defn asHtmlCalculation
  "Creo un PRE html con l'operazione ed il
  risultato in bianco.
  "
  [[expr _] {:keys [:fontsize]}]
  [:pre (style {:font-size  (str fontsize "px")})
   (str expr " = ____")])

(defn creaTabella
  "Crea la mia tabella,
  "
  [fnInput {:keys [:columns :rows] :as parametri}]

  (let [fnDaApplicare (fn [] (fnInput parametri))
        entries (* rows columns)
        allEntries (take-unique fnDaApplicare entries)
        allRows (partition columns columns [] allEntries)]

    [:table
     (for [r allRows]
       [:tr
        (for [e r]
          [:td
           (style {:padding "10px"})
           (asHtmlCalculation e parametri)])])]))

(defn salvaHtml
  [fnInput {:keys [:file] :as parametri}]

  (let [tbl (creaTabella fnInput parametri)
        pg [:html
            [:body
             [:h1 "Tabellina"]
             tbl]]
        txt (html pg)]

    (spit file txt
          :append false)))

(def CONFIGURATION
  {:app         {:command     (get-in BUILD [:project :project])
                 :description "Crea i fogli di tabelline"
                 :version     (get-in BUILD [:project :version])}

   :global-opts [{:option  "file"
                  :as      "Il file da generare"
                  :type    :string
                  :default "tabellina.html"}

                 {:option  "columns"
                  :as      "Il numero di colonne"
                  :type    :int
                  :default 3}

                 {:option  "rows"
                  :as      "Il numero di righe per colonna"
                  :type    :int
                  :default 30}

                 {:option  "fontsize"
                  :as      "Il numero di righe per colonna"
                  :type    :int
                  :default 15}]

   :commands    [{:command     "muls"
                  :description "Le moltiplicazioni classiche"
                  :opts        [{:option "min" :as "Minimo" :type :int :default 0}
                                {:option "max" :as "Massimo" :type :int :default 11}]
                  :runs        (partial salvaHtml multiplications)}

                 {:command     "divsimpl"
                  :description "Divisioni senza resto"
                  :opts        [{:option "min" :as "Minimo" :type :int :default 0}
                                {:option "max" :as "Massimo" :type :int :default 11}]
                  :runs        (partial salvaHtml divisioni-semplici)}]})

(defn -main
  "This is our entry point.
Just pass parameters and configuration.
Commands (functions) will be invoked as appropriate."
  [& args]
  (run-cmd args CONFIGURATION))