/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "af8zjlud",
    "name": "deadline",
    "type": "date",
    "required": false,
    "presentable": false,
    "unique": false,
    "options": {
      "min": "",
      "max": ""
    }
  }))

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d")

  // remove
  collection.schema.removeField("af8zjlud")

  return dao.saveCollection(collection)
})
