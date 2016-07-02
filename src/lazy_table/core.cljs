(ns lazy-table.core
  (:require [reagent.core :as reagent]))

(defn- update-val-if-greater
  [val-item count-item new-val dividend]
  (when (> new-val @val-item)
    (reset! val-item new-val)
    (reset! count-item (/ new-val dividend))))

(defn- scroll-event
  [display-height display-width min-row-height min-col-width row-count col-count e]
  (let [element (.-target e)
        el-bcr (.getBoundingClientRect element)
        new-height (+ (.-height el-bcr) (.-scrollTop element))
        new-width (+ (.-width el-bcr) (.-scrollLeft element))]
    (update-val-if-greater display-height row-count new-height min-row-height)
    (update-val-if-greater display-width col-count new-width min-col-width)))

(defn- initialize-width
  [display-width min-col-width col-count this]
  (let [new-width (-> this reagent/dom-node .getBoundingClientRect .-width)]
    (reset! display-width new-width)
    (reset! col-count (/ new-width min-col-width))))

(defn lazy-table
  [{:keys [id classes table-height min-row-height min-col-width table-fn
           additional-scroll-fn inline-style reset-scroll-on-props-change? no-inline-style?]

    :or   {table-height                  500
           min-row-height                1
           min-col-width                 1
           inline-style                  {:height      "500px"
                                          :width       "500px"
                                          :overflow-y  "scroll"
                                          :overflow-x  "scroll"
                                          :white-space "nowrap"}
           reset-scroll-on-props-change? true}}]

  (let [display-height (reagent/atom table-height)
        display-width (reagent/atom 0)
        row-count (reagent/atom 0)
        col-count (reagent/atom 0)
        initialize-width (partial initialize-width display-width min-col-width col-count)
        scroll-event (partial scroll-event display-height display-width min-row-height min-col-width row-count col-count)]

    (reagent/create-class
      {:component-will-mount         (fn []
                                       (reset! row-count (/ table-height min-row-height)))
       :component-did-mount          initialize-width
       :component-will-receive-props (fn [this _]
                                       (when reset-scroll-on-props-change?
                                         (reset! row-count (/ table-height min-row-height))
                                         (initialize-width this)))
       :reagent-render               (fn []
                                       [:div
                                        {:id        id
                                         :class     classes
                                         :on-scroll (fn [e]
                                                      (scroll-event e)
                                                      (when additional-scroll-fn
                                                        (additional-scroll-fn)))
                                         :style     (when-not no-inline-style?
                                                      inline-style)}
                                        (if (coll? table-fn)
                                          (for [tfn table-fn]
                                            ^{:key (pr-str tfn)}
                                            [tfn row-count col-count])
                                          [table-fn row-count col-count])])})))