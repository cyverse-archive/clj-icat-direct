(ns clj-icat-direct.icat
  (:require [clojure.string :as string]
            [korma.db :as db]
            [korma.core :as k]
            [clj-icat-direct.queries :as q]))

(defn icat-db-spec
  "Creates a Korma db spec for the ICAT."
  [hostname user pass & {:keys [port db]
                         :or {port 5432
                              db "ICAT"}}]
  (db/postgres {:host     hostname
                :port     port
                :db       db
                :user     user
                :password pass}))

(defn setup-icat
  "Defines the icat database. Pass in the return value of icat-db-spec."
  [icat-db-spec]
  (db/defdb icat icat-db-spec))

(defn- run-simple-query
  "Runs one of the defined queries against the ICAT. It's considered a simple query if it doesn't
   require string formatting."
  [query-kw & args]
  (if-not (contains? q/queries query-kw)
    (throw (Exception. (str "query " query-kw " is not defined."))))
  
  (k/exec-raw icat [(get q/queries query-kw) args] :results))

(defn- run-query-string
  "Runs the passed in query string. Doesn't check to see if it's defined in 
   clj-icat-direct.queries first."
  [query & args]
  (k/exec-raw icat [query args] :results))

(defn number-of-files-in-folder
  "Returns the number of files in a folder that the user has access to."
  [user folder-path]
  (-> (run-simple-query :count-files-in-folder user folder-path) first :count))

(defn number-of-folders-in-folder
  "Returns the number of folders in the specified folder that the user has access to."
  [user folder-path]
  (-> (run-simple-query :count-folders-in-folder user folder-path) first :count))

(defn number-of-items-in-folder
  "Returns the total number of files and folders in the specified folder that the user has access to."
  [user folder-path]
  (-> (run-simple-query :count-items-in-folder user folder-path) first :count))

(defn list-folders-in-folder
  "Returns a listing of the folders contained in the specified folder that the user has access to."
  [user folder-path]
  (run-simple-query :list-folders-in-folder user folder-path))

(def sort-columns
  {:type      "p.type"
   :modify-ts "p.modify_ts"
   :create-ts "p.create_ts"
   :data-size "p.data_size"
   :base-name "p.base_name"
   :full-path "p.full_path"})

(def sort-orders
  {:asc  "ASC"
   :desc "DESC"})

(defn paged-folder-listing
  "Returns a page from a folder listing."
  [user folder-path sort-column sort-order limit offset]
  (if-not (contains? sort-columns sort-column)
    (throw (Exception. (str "Invalid sort-column " sort-column))))
  
  (if-not (contains? sort-orders sort-order)
    (throw (Exception. (str "Invalid sort-order " sort-order))))
  
  (let [sc    (get sort-columns sort-column)
        so    (get sort-orders sort-order)
        query (format (:paged-folder-listing q/queries) sc so)]
    (run-query-string query user folder-path limit offset)))
