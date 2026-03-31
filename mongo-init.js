db = db.getSiblingDB('franchisedb');

db.createUser({
    user: 'franchise_user',
    pwd: 'franchise_pass',
    roles: [{ role: 'readWrite', db: 'franchisedb' }]
});

db.createCollection('franchises');
