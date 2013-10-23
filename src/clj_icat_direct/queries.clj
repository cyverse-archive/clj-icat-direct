(ns clj-icat-direct.queries)

(def queries
  {:count-items-in-folder
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT COUNT(p.*)
      FROM ( SELECT c.coll_name      as dir_name,
                    d.data_path      as full_path,
                    d.data_name      as base_name,
                    d.create_ts      as create_ts, 
                    d.modify_ts      as modify_ts,
                    'dataobject'     as type,
                    d.data_size      as data_size
               FROM r_data_main d
               JOIN r_coll_main c ON c.coll_id = d.coll_id 
               JOIN r_objt_access a ON d.data_id = a.object_id
               JOIN r_user_main u ON a.user_id = u.user_id,
                    user_lookup,
                    parent
              WHERE u.user_id IN ( SELECT g.group_user_id FROM r_user_main u JOIN r_user_group g ON g.user_id = u.user_id, user_lookup WHERE u.user_id = user_lookup.user_id )
                AND c.coll_id = parent.coll_id
              UNION
             SELECT c.parent_coll_name as dir_name,
                    c.coll_name        as full_path,
                    regexp_replace(c.coll_name, '.*/', '') as base_name,
                    c.create_ts        as create_ts, 
                    c.modify_ts        as modify_ts,
                    'collection'       as type,
                    0                  as data_size
               FROM r_coll_main c 
               JOIN r_objt_access a ON c.coll_id = a.object_id
               JOIN r_user_main u ON a.user_id = u.user_id,
                    user_lookup,
                    parent
              WHERE u.user_id IN ( SELECT g.group_user_id FROM r_user_main u JOIN r_user_group g ON g.user_id = u.user_id, user_lookup WHERE u.user_id = user_lookup.user_id )
                AND c.parent_coll_name = parent.coll_name
                AND c.coll_type != 'linkPoint') AS p"
   
   :list-folders-in-folder
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT DISTINCT c.parent_coll_name as dir_name,
           c.coll_name        as full_path,
           regexp_replace(c.coll_name, '.*/', '') as base_name,
           c.create_ts        as create_ts, 
           c.modify_ts        as modify_ts,
           'collection'       as type,
           0                  as data_size
      FROM r_coll_main c 
      JOIN r_objt_access a ON c.coll_id = a.object_id
      JOIN r_user_main u ON a.user_id = u.user_id,
           user_lookup,
           parent
     WHERE u.user_id IN ( SELECT g.group_user_id 
                           FROM  r_user_group g,
                                 user_lookup
                           WHERE g.user_id = user_lookup.user_id )
       AND c.parent_coll_name = parent.coll_name
       AND c.coll_type != 'linkPoint'
  ORDER BY base_name ASC"
   
   :count-files-in-folder
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT COUNT(DISTINCT d.data_name)
      FROM r_data_main d
      JOIN r_coll_main c ON c.coll_id = d.coll_id 
      JOIN r_objt_access a ON d.data_id = a.object_id
      JOIN r_user_main u ON a.user_id = u.user_id,
           user_lookup,
           parent
     WHERE u.user_id IN ( SELECT g.group_user_id 
                            FROM r_user_group g,
                                 user_lookup
                           WHERE g.user_id = user_lookup.user_id )
       AND c.coll_id = parent.coll_id"
   
   :count-folders-in-folder
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT COUNT(DISTINCT c.coll_name)
      FROM r_coll_main c
      JOIN r_objt_access a ON c.coll_id = a.object_id
      JOIN r_user_main u ON a.user_id = u.user_id,
           user_lookup,
           parent
     WHERE u.user_id IN ( SELECT g.group_user_id 
                            FROM  r_user_group g,
                                  user_lookup
                            WHERE g.user_id = user_lookup.user_id )
       AND c.parent_coll_name = parent.coll_name
       AND c.coll_type != 'linkPoint'"
   
   :file-permissions
   "SELECT distinct o.access_type_id, u.user_name 
      FROM r_user_main u,
           r_data_main d,
           r_coll_main c,
           r_tokn_main t,
           r_objt_access o  
     WHERE c.coll_name = ?
       AND d.data_name = ?
       AND c.coll_id = d.coll_id
       AND o.object_id = d.data_id
       AND t.token_namespace = 'access_type'
       AND u.user_id = o.user_id
       AND o.access_type_id = t.token_id
     LIMIT ?
    OFFSET ?"
   
   :folder-permissions
   "SELECT a.access_type_id, u.user_name 
     FROM r_coll_main c
     JOIN r_objt_access a ON c.coll_id = a.object_id
     JOIN r_user_main u ON a.user_id = u.user_id
    WHERE c.parent_coll_name = ?
      AND c.coll_name = ?
    LIMIT ?
   OFFSET ?"
   
   :folder-permissions-for-user
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?)
    SELECT a.access_type_id
      FROM r_coll_main c
      JOIN r_objt_access a ON c.coll_id = a.object_id
      JOIN r_user_main u ON a.user_id = u.user_id
     WHERE c.coll_name = ?
       AND u.user_id IN ( SELECT g.group_user_id 
                           FROM  r_user_group g,
                                 user_lookup
                           WHERE g.user_id = user_lookup.user_id )"
   
   :file-permissions-for-user
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ? ),
              parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT a.access_type_id
      FROM r_data_main d
      JOIN r_coll_main c ON c.coll_id = d.coll_id
      JOIN r_objt_access a ON d.data_id = a.object_id
      JOIN r_user_main u ON a.user_id = u.user_id,
           user_lookup,
           parent
     WHERE u.user_id IN ( SELECT g.group_user_id 
                           FROM  r_user_group g,
                                 user_lookup
                           WHERE g.user_id = user_lookup.user_id )
       AND c.coll_id = parent.coll_id
       AND d.data_name = ?"
   
   :paged-folder-listing
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT DISTINCT p.full_path, p.base_name, p.data_size, p.create_ts, p.modify_ts, p.type
      FROM ( SELECT c.coll_name      as dir_name,
                    c.coll_name || '/' || d.data_name as full_path,
                    d.data_name      as base_name,
                    d.create_ts      as create_ts, 
                    d.modify_ts      as modify_ts,
                    'dataobject'     as type,
                    d.data_size      as data_size,
                    a.access_type_id as access_type_id
               FROM r_data_main d
               JOIN r_coll_main c ON c.coll_id = d.coll_id 
               JOIN r_objt_access a ON d.data_id = a.object_id
               JOIN r_user_main u ON a.user_id = u.user_id,
                    user_lookup,
                    parent
              WHERE u.user_id IN ( SELECT g.group_user_id FROM r_user_main u JOIN r_user_group g ON g.user_id = u.user_id, user_lookup WHERE u.user_id = user_lookup.user_id )
                AND c.coll_id = parent.coll_id
              UNION
             SELECT c.parent_coll_name as dir_name,
                    c.coll_name        as full_path,
                    regexp_replace(c.coll_name, '.*/', '') as base_name,
                    c.create_ts        as create_ts, 
                    c.modify_ts        as modify_ts,
                    'collection'       as type,
                    0                  as data_size,
                    a.access_type_id   as access_type_id
               FROM r_coll_main c 
               JOIN r_objt_access a ON c.coll_id = a.object_id
               JOIN r_user_main u ON a.user_id = u.user_id,
                    user_lookup,
                    parent
              WHERE u.user_id IN ( SELECT g.group_user_id FROM r_user_main u JOIN r_user_group g ON g.user_id = u.user_id, user_lookup WHERE u.user_id = user_lookup.user_id )
                AND c.parent_coll_name = parent.coll_name
                AND c.coll_type != 'linkPoint' ) AS p
    ORDER BY p.type ASC, %s %s
       LIMIT ?
      OFFSET ?"})