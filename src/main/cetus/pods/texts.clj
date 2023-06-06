(ns cetus.pods.texts)

(defn group-crn? [s] 
  (seq? (re-seq #"urn:cts:(\w+):(\w+)$" s)))

(defn work-crn? [s] 
  (seq? (re-seq #"urn:cts:(\w+):(\w+)\.([a-zA-Z0-9\-]+)" s)))

(defn crn? [s]
  (or (group-crn? s) (work-crn? s)))


(def routes
  ["/texts"
   ["" {:get {:handler (fn [_] {:status 200
                                :headers {"content-type" "text/html"}
                                :body "all texts"})}}]
   ["/search" {:get {:parameters {:query {:q string?}}
                     :handler (fn [{{{:keys [q]} :query} :parameters}] 
                                {:status 200
                                 :headers {"content-type" "text/html"}
                                 :body (str "search query " q)})}}]
   ["/group/:id" {:get {:parameters {:path {:id group-crn?}}
                        :handler (fn [{{{:keys [id]} :path} :parameters}]
                                   {:status 200
                                    :headers {"content-type" "text/html"}
                                    :body (str "group " id)})}}]
   ["/work/:id" {:get {:parameters {:path {:id work-crn?}}
                        :handler (fn [{{{:keys [id]} :path} :parameters}]
                                   {:status 200
                                    :headers {"content-type" "text/html"}
                                    :body (str "work " id)})}}]

   ])

(defn start! [] :ok)
(defn stop! [_] :ok)
