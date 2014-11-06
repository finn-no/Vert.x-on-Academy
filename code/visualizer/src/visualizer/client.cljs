(ns visualizer.client
  (:require [vertx.client.eventbus :as eb]
            [enfocus.core :as ef]
            [enfocus.events :as events])
  (:require-macros [enfocus.macros :as em]))

(def eb (atom nil))

(defn url-to-eventbus []
  (str js/window.location.protocol "//"
       js/window.location.hostname ":"
       js/window.location.port "/eventbus"))

(defn open-eventbus []
  (let [eventbus (eb/eventbus (url-to-eventbus))]
      (eb/on-open eventbus (fn [bus]
                             (reset! eb bus)
                             (subscribe "filtered_message")
                             (js/console.log "Eventbus opened")))
      (eb/on-close eventbus (fn []
                              (reset! eb nil)))))

(defn close-eventbus []
  (when @eb
    (eb/close @eb)))

(defn append-text [id txt]
  (ef/at
       id (ef/append
           (ef/html [:li txt]))))

(defn li-elements []
  (.getElementsByTagName js/document "li"))

(defn last-li-element-index []
  (- (.-length (li-elements)) 1))

(defn last-li-element []
  (aget (li-elements) (last-li-element-index)))

(defn scroll-into-view []
  (.scrollIntoView (last-li-element)))

(defn display-message [id message]
  (js/console.log message)
  (append-text id message)
  (scroll-into-view))

(defn publish-filter [message]
  (when @eb
    (eb/publish @eb "filter" message)
    (js/console.log (str "Published: " message))))

(defn subscribe [addr]
  (when @eb
    (eb/on-message @eb addr (partial display-message "#content"))))

(defn get-value [id]
  (ef/from id (ef/get-prop :value)))

(defn on-click [f]
  (fn [node]
    ((events/listen :click f) node)))

(defn init []
  (open-eventbus)
  (ef/at
    "#filterMessages"  (on-click
                         #(publish-filter (get-value "#filterValue")))))

(set! (.-onload js/window) init)
