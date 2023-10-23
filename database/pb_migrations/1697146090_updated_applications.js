/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("7mhia5h374a1ls5")

  // update
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "zzhgwfyy",
    "name": "data",
    "type": "json",
    "required": false,
    "presentable": false,
    "unique": false,
    "options": {}
  }))

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("7mhia5h374a1ls5")

  // update
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "zzhgwfyy",
    "name": "data",
    "type": "json",
    "required": true,
    "presentable": false,
    "unique": false,
    "options": {}
  }))

  return dao.saveCollection(collection)
})
