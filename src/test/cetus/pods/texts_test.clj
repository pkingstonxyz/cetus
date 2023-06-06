(ns cetus.pods.texts-test
  (:require [clojure.test :refer [deftest testing is]]
            [cetus.pods.texts :as texts]))

(deftest ^:unit predicates
  (let [group-crns ["urn:cts:ancJewLit:babTalmud" "urn:cts:ancJewLit:hebBible" "urn:cts:ancJewLit:mishnah" 
                    "urn:cts:farsiLit:hafez" "urn:cts:greekLit:ggm0001" "urn:cts:greekLit:ogl0001" 
                    "urn:cts:greekLit:stoa0033a" "urn:cts:greekLit:stoa0121" "urn:cts:greekLit:stoa0146d" 
                    "urn:cts:greekLit:tlg0001" "urn:cts:greekLit:tlg4084" "urn:cts:greekLit:tlg4086" 
                    "urn:cts:greekLit:tlg4089" "urn:cts:greekLit:tlg4090" "urn:cts:greekLit:tlg4102"
                    "urn:cts:greekLit:tlg9019" "urn:cts:hebrewlit:heb0001" "urn:cts:latinLit:phi0119" 
                    "urn:cts:latinLit:phi0134" "urn:cts:latinLit:phi0448" "urn:cts:latinLit:phi1318" 
                    "urn:cts:latinLit:phi1345" "urn:cts:latinLit:phi1348" "urn:cts:latinLit:phi1351" 
                    "urn:cts:latinLit:phi2003" "urn:cts:latinLit:phi2331" "urn:cts:latinLit:phi9500" 
                    "urn:cts:latinLit:stoa0007" "urn:cts:latinLit:stoa0022" "urn:cts:latinLit:stoa0022a"
                    "urn:cts:latinLit:stoa0329" "urn:cts:latinLit:stoa0329c" "urn:cts:latinLit:stoa0367"
                    "urn:cts:mayaLit:lit0001" "urn:cts:pdlpsci:bodin" "urn:cts:perslit:anvari" 
                    "urn:cts:perslit:attar" "urn:cts:perslit:babaafzal" "urn:cts:perslit:babataher"
                    "urn:cts:perslit:bahaee" "urn:cts:perslit:saeb" "urn:cts:perslit:salman"
                    "urn:cts:perslit:sanaee" "urn:cts:perslit:seyf" "urn:cts:perslit:shabestari"
                    "urn:cts:perslit:shahnematollah" "urn:cts:perslit:shahriar" "urn:cts:perslit:shater"]
        work-crns ["urn:cts:ancJewLit:babTalmud.bavli" "urn:cts:ancJewLit:hebBible.tanakh" 
                   "urn:cts:ancJewLit:mishnah.mishnah" "urn:cts:farsiLit:hafez.divan" 
                   "urn:cts:greekLit:ggm0001.ggm001" "urn:cts:greekLit:ogl0001.ogl001" 
                   "urn:cts:greekLit:stoa0033a.stoa0033a" "urn:cts:greekLit:stoa0121.stoa0121" 
                   "urn:cts:greekLit:stoa0146d.stoa0146d" "urn:cts:greekLit:tlg0001.tlg001" 
                   "urn:cts:hebrewlit:heb0001.heb001" "urn:cts:latinLit:phi0119.phi001" 
                   "urn:cts:latinLit:phi0134.phi001" "urn:cts:latinLit:phi0448.phi001" 
                   "urn:cts:latinLit:phi9500.phi001" "urn:cts:latinLit:stoa0007.stoa0007" 
                   "urn:cts:latinLit:stoa0022.stoa0022" "urn:cts:latinLit:stoa0022a.stoa0022"]]
    (testing "group-crn?"
      (is (every? texts/group-crn? group-crns) "All Group CRNs should be valid"))
    (testing "work-crn?"
      (is (every? texts/work-crn? work-crns) "All Work CRNs should be valid"))))

