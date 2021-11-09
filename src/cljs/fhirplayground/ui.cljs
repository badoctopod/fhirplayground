(ns fhirplayground.ui
  (:require
   ["@ant-design/icons" :refer (UserAddOutlined
                                MessageOutlined)]
   ["antd" :refer (Button Typography Layout Divider Row Col Space
                   PageHeader Input Form Avatar Descriptions Empty
                   List Tabs Tag Timeline Tooltip ConfigProvider
                   Spin Popconfirm message)]
   [goog.object :as gobj]
   [reagent.core :as reagent]))

;; GENERAL ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def button (reagent/adapt-react-class Button))

(def typography (reagent/adapt-react-class Typography))
(def typography-title (reagent/adapt-react-class
                       (gobj/get
                        js/fhirplayground.ui.typography.tag
                        "Title")))
(def typography-paragraph (reagent/adapt-react-class
                           (gobj/get
                            js/fhirplayground.ui.typography.tag
                            "Paragraph")))
(def typography-text (reagent/adapt-react-class
                      (gobj/get
                       js/fhirplayground.ui.typography.tag
                       "Text")))
(def typography-link (reagent/adapt-react-class
                      (gobj/get
                       js/fhirplayground.ui.typography.tag
                       "Link")))

;; LAYOUT ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def layout (reagent/adapt-react-class Layout))
(def layout-header (reagent/adapt-react-class
                    (gobj/get
                     js/fhirplayground.ui.layout.tag
                     "Header")))
(def layout-content (reagent/adapt-react-class
                     (gobj/get
                      js/fhirplayground.ui.layout.tag
                      "Content")))

(def divider (reagent/adapt-react-class Divider))

(def row (reagent/adapt-react-class Row))

(def col (reagent/adapt-react-class Col))

(def space (reagent/adapt-react-class Space))

;; NAVIGATION ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def page-header (reagent/adapt-react-class PageHeader))

;; DATA ENTRY ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def input (reagent/adapt-react-class Input))
(def input-search (reagent/adapt-react-class
                   (gobj/get
                    js/fhirplayground.ui.input.tag
                    "Search")))

(def form (reagent/adapt-react-class Form))
(def form-item (reagent/adapt-react-class
                (gobj/get
                 js/fhirplayground.ui.form.tag
                 "Item")))

;; DATA DISPLAY ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def avatar (reagent/adapt-react-class Avatar))

(def descriptions (reagent/adapt-react-class Descriptions))
(def descriptions-item (reagent/adapt-react-class
                        (gobj/get
                         js/fhirplayground.ui.descriptions.tag
                         "Item")))

(def no-content (reagent/adapt-react-class Empty))

(def simple-list (reagent/adapt-react-class List))
(def simple-list-item (reagent/adapt-react-class
                       (gobj/get
                        js/fhirplayground.ui.simple_list.tag
                        "Item")))
(def simple-list-item-meta (reagent/adapt-react-class
                            (gobj/get
                             js/fhirplayground.ui.simple_list_item.tag
                             "Meta")))

(def tabs (reagent/adapt-react-class Tabs))
(def tabs-tab-pane (reagent/adapt-react-class
                    (gobj/get
                     js/fhirplayground.ui.tabs.tag
                     "TabPane")))

(def tag (reagent/adapt-react-class Tag))

(def timeline (reagent/adapt-react-class Timeline))
(def timeline-item (reagent/adapt-react-class
                    (gobj/get
                     js/fhirplayground.ui.timeline.tag
                     "Item")))

(def tooltip (reagent/adapt-react-class Tooltip))

;; FEEDBACK
(def spin (reagent/adapt-react-class Spin))
(def popconfirm (reagent/adapt-react-class Popconfirm))
(def show-message message)

;; OTHER ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def config-provider (reagent/adapt-react-class ConfigProvider))

;; ICONS ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def user-add-outlined (reagent/adapt-react-class UserAddOutlined))
(def message-outlined (reagent/adapt-react-class MessageOutlined))
