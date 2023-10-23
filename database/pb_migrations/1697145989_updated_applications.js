/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("7mhia5h374a1ls5")

  collection.listRule = "applicant = @request.auth.id"
  collection.viewRule = "applicant = @request.auth.id"
  collection.createRule = ""

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("7mhia5h374a1ls5")

  collection.listRule = ""
  collection.viewRule = null
  collection.createRule = null

  return dao.saveCollection(collection)
})
