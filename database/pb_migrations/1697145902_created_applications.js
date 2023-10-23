/// <reference path="../pb_data/types.d.ts" />
migrate((db) => {
  const collection = new Collection({
    "id": "7mhia5h374a1ls5",
    "created": "2023-10-12 21:25:02.064Z",
    "updated": "2023-10-12 21:25:02.064Z",
    "name": "applications",
    "type": "base",
    "system": false,
    "schema": [
      {
        "system": false,
        "id": "jqnth4sq",
        "name": "applicant",
        "type": "relation",
        "required": true,
        "presentable": false,
        "unique": false,
        "options": {
          "collectionId": "_pb_users_auth_",
          "cascadeDelete": false,
          "minSelect": null,
          "maxSelect": 1,
          "displayFields": null
        }
      },
      {
        "system": false,
        "id": "eplkbhtq",
        "name": "scholarship",
        "type": "relation",
        "required": true,
        "presentable": false,
        "unique": false,
        "options": {
          "collectionId": "s1g9z65itutte7d",
          "cascadeDelete": false,
          "minSelect": null,
          "maxSelect": 1,
          "displayFields": null
        }
      },
      {
        "system": false,
        "id": "zzhgwfyy",
        "name": "data",
        "type": "json",
        "required": true,
        "presentable": false,
        "unique": false,
        "options": {}
      },
      {
        "system": false,
        "id": "iv37p9pq",
        "name": "state",
        "type": "select",
        "required": true,
        "presentable": false,
        "unique": false,
        "options": {
          "maxSelect": 1,
          "values": [
            "received",
            "in-review",
            "rejected",
            "accepted"
          ]
        }
      }
    ],
    "indexes": [],
    "listRule": "",
    "viewRule": null,
    "createRule": null,
    "updateRule": null,
    "deleteRule": null,
    "options": {}
  });

  return Dao(db).saveCollection(collection);
}, (db) => {
  const dao = new Dao(db);
  const collection = dao.findCollectionByNameOrId("7mhia5h374a1ls5");

  return dao.deleteCollection(collection);
})
