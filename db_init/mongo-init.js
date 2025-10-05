db.auth('mrlk_usr', 'mrlk_pwd')

db = db.getSiblingDB('libhomedb')

db.createUser({
  user: 'mrlk_u',
  pwd: 'mrlk_p',
  roles: [
    {
      role: 'readWrite',
      db: 'libhomedb',
    },
  ],
});
