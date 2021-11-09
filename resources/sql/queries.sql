-- :name get-patient-by-id
-- :doc Returns `Patient` by given `id`. Expects `id` to be a string. Example: `(get-patient-by-id datasource {:id "string"})`.
-- :command :query
-- :result :one
SELECT resource::text FROM patient WHERE id = :id

-- :name search-patient-by-family-name
-- :doc Returns subset of `Patient` by given `family` name. Expects `family` to be a string. Example: `(search-patient-by-family-name datasource {:family "Silos234"})`.
-- :command :query
-- :result :many

SELECT
  resource->>'id' AS id,
  resource->>'gender' AS gender,
  resource->>'birthDate' AS birthdate,
  patient_name::text AS name
FROM
  patient,
  jsonb_path_query(
    resource,
    (
      SELECT
        '$.name ? (@.family like_regex "^' ||
        :family                            ||
        '"'                                ||
        ' flag "i") ? (@.use == "official")'
    )::jsonpath
  ) patient_name

-- :name delete-patient
-- :doc Deletes `Patient` from resource table and moves record to history table. Example: `(delete-patient {:id "patient-id" :resource-type "Patient"})`.
-- :command :query
-- :result :one

SELECT fhirbase_delete(:resource-type, :patient-id)
