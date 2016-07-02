# Lazy Table

[![Clojars Project](http://clojars.org/lazy-table/latest-version.svg)](https://clojars.org/lazy-table)

Lazy rendering for [reagent](https://github.com/reagent-project/reagent) tables and lists.

This library was built as an abstraction from a lazy table implentation I made for a complex 'table' (series of tables within an overflow div, actually). Existing react implentations for lazy lists or tables seemed to be too rigid and unflexible for what I needed. Hence the introduction of this library.

[Examples](https://github.com/nikolap/lazy-table-examples)

## Usage

To begin using you must required lazy-table.core in your namespace, e.g. `(:require [lazy-table.core :refer [lazy-table]])`

From there creating a lazy table requires just one thing: a UI component, i.e. just a basic reagent function. The component should take two parameters (or props) passed in by lazy-table: row-count and col-count. These determine how many rows and cols of your entire data to display. See below for an example implentation (note: re-frame is not required).

```clojure
(defn basic-table-body [row-count col-count]
  (let [data (re-frame/subscribe [:table-data])]
    (fn [row-count col-count]
      [:table>tbody
       (doall
         (for [row (take @row-count @data)]
           [:tr
            (for [cell (take @col-count row)]
              [:td cell])]))])))
``` 

Once your have your table body you can create a table just by calling

```
[lazy-table {:table-fn basic-table-body}]
```

However, you'll likely notice that it's not too lazy a table. This is because of some of our default parameters (see later below). We will need to set some styling information about our table/list in order for it to lazily render. Let's assume that our table row height will be roughly 15px.

```
[lazy-table {:table-fn       basic-table-body
             :min-row-height 15}]
```

That's it for a basic table! We'll explore some of the additional parameters further down.

### Default parameters
```clojure
[lazy-table
 {:id                            nil
  :classes                       nil
  :table-height                  500
  :min-row-height                1
  :min-col-width                 1
  :additional-scroll-fn          nil
  :inline-style                  {:height      "500px"
                                  :width       "500px"
                                  :overflow-y  "scroll"
                                  :overflow-x  "scroll"
                                  :white-space "nowrap"}
  :reset-scroll-on-props-change? true
  :no-inline-stlye?              nil
  :table-fn                      nil}] 					;; table-fn is required!
```

### Additional parameters
```clojure
{
;; Sets the thing you will be lazily rendering. Typically this is just a function, but let's say you want several elements to be wrapped here, you can pass through a list of functions
:table-fn 						one-fn-call
:table-fn						[lots-of-table-fns 
								 lots-and-lots-of-table-fns]

;; Set the id of the enclosing div
:id 							"my-id"

;; Set the class(es) of the encosing div
:classes 						"one-class two-class cow-class"

;; Sets the initial table height, in pixels, to be used when mounting the component. This should, generally speaking, be the height of your container div
:table-height 					9001

;; Sets the dividend for determining the rows to display. Essentially you take the height of the table + scrolled value and divide by this value. In pixels
:min-row-height					50

;; Same principle as min-row-height, but for horizontal scrolling. In pixels
:min-col-height					50

;; Should you have an additional function to call (typically with side effects) during the on-scroll event for the container div, you can enter it here
:additional-scroll-fn 			#(prn "I am so smart, S-M-R-T")

;; Flag to enable/disable resetting of rendered rows and columns on props change. On by default
:reset-scroll-on-props-change? 	true

;; Flag to disable inline styling. Off by default. You would typically turn this on if you have CSS styling ready for your container div and are assigning it via classes or ids.
:no-inline-stlye? 				false
}
```

## How it works

lazy-table is a reagent class that wraps a div around any number of elements (each that take a row-count and col-count parameter).  The div should ideally be scrollable in order for you to get any good use out of it.... When it is, every time a scroll event happens there is a check to see if the scroll is greater than what has been rendered thus far. If so, then the value needs to be incremented accordingly. If rows/cols have already been rendered they will not get removed when the scroll is moved backwards.

Whenever the props of the table are updated (typically this means a change in data, but not always! Thus you can turn this function off), lazy-table will, by default, reset the rows and cols it displays.

This library is a more user-friendly abstraction of a lazy table implementation built for a complicated table (imagine a div containing three separate tables, with custom scroll events).

## Limitations

There are two flaws with this library. Suggestions and/or PRs to help address these are more than welcome:

1. Styling information present in not-styling code. For lazy-table to work, you need to pass through the minimum heights and widths for rows and columns. Ideally, you would be able to programmatically determine this but there is yet to be an elegant solution. Especially given that widths and heights can vary (imagine long text that wraps around a cell). You also need to pass through the table height which may not be properly discovered via getBoundingClientRect in some cases.
2. The row-count and col-count information needs to be interleaved into your table-fn UI component. While this isn't as bad as coupling on the bad end, I would like to further decouple this. That said, there doesn't seem to be a clear way to both decouple and keep the flexibility of the library in tact, i.e. say you wanted to have multiple tables handled somehow, or say that I have some unusual, abstract data structure.

## License

Copyright © 2016 Nikola Peric

Distributed under the MIT License
