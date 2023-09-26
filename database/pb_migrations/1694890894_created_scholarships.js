/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const collection = new Collection({
    "id": "s1g9z65itutte7d",
    "created": "2023-09-16 19:01:34.871Z",
    "updated": "2023-09-16 19:01:34.871Z",
    "name": "scholarships",
    "type": "base",
    "system": false,
    "schema": [
      {
        "system": false,
        "id": "bgt1fkus",
        "name": "name",
        "type": "text",
        "required": false,
        "presentable": false,
        "unique": false,
        "options": {
          "min": null,
          "max": null,
          "pattern": ""
        }
      },
      {
        "system": false,
        "id": "f2rgux1n",
        "name": "description",
        "type": "text",
        "required": false,
        "presentable": false,
        "unique": false,
        "options": {
          "min": null,
          "max": null,
          "pattern": ""
        }
      }
    ],
    "indexes": [],
    "listRule": "",
    "viewRule": "",
    "createRule": null,
    "updateRule": null,
    "deleteRule": null,
    "options": {}
  });

  return Dao(db).saveCollection(collection);
}, (db) => {
  const dao = new Dao(db);
  const collection = dao.findCollectionByNameOrId("s1g9z65itutte7d");

  return dao.deleteCollection(collection);
})
