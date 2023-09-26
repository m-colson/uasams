/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d")

  collection.updateRule = "@request.auth.id = owner.id"
  collection.deleteRule = "@request.auth.id = owner.id"

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "8nifsi1w",
    "name": "owner",
    "type": "relation",
    "required": false,
    "presentable": false,
    "unique": false,
    "options": {
      "collectionId": "_pb_users_auth_",
      "cascadeDelete": false,
      "minSelect": null,
      "maxSelect": 1,
      "displayFields": null
    }
  }))

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d")

  collection.updateRule = null
  collection.deleteRule = null

  // remove
  collection.schema.removeField("8nifsi1w")

  return dao.saveCollection(collection)
})
