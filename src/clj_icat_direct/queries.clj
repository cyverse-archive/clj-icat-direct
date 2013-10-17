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
                    d.data_size      as data_size,
                    a.access_type_id as access_type_id
               FROM r_data_main d
               JOIN r_coll_main c ON c.coll_id = d.coll_id 
               JOIN r_objt_access a ON d.data_id = a.object_id
               JOIN r_user_main u ON a.user_id = u.user_id,
                    user_lookup,
                    parent
              WHERE u.user_id = user_lookup.user_id
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
              WHERE u.user_id = user_lookup.user_id
                AND c.parent_coll_name = parent.coll_name
                AND c.coll_type != 'linkPoint') AS p"
   
   :list-folders-in-folder
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
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
     WHERE u.user_id = user_lookup.user_id
       AND c.parent_coll_name = parent.coll_name
       AND c.coll_type != 'linkPoint'"
   
   :count-files-in-folder
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT COUNT(*)
      FROM r_data_main d
      JOIN r_coll_main c ON c.coll_id = d.coll_id 
      JOIN r_objt_access a ON d.data_id = a.object_id
      JOIN r_user_main u ON a.user_id = u.user_id,
           user_lookup,
           parent
     WHERE u.user_id = user_lookup.user_id
       AND c.coll_id = parent.coll_id"
   
   :count-folders-in-folder
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT COUNT(*)
      FROM r_coll_main c
      JOIN r_objt_access a ON c.coll_id = a.object_id
      JOIN r_user_main u ON a.user_id = u.user_id,
           user_lookup,
           parent
     WHERE u.user_id = user_lookup.user_id
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
   
   :paged-folder-listing
   "WITH user_lookup AS ( SELECT u.user_id as user_id FROM r_user_main u WHERE u.user_name = ?),
         parent AS ( SELECT c.coll_id as coll_id, c.coll_name as coll_name FROM r_coll_main c WHERE c.coll_name = ? )
    SELECT p.full_path, p.base_name, p.data_size, p.create_ts, p.modify_ts, p.access_type_id, p.type
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
              WHERE u.user_id = user_lookup.user_id
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
              WHERE u.user_id = user_lookup.user_id
                AND c.parent_coll_name = parent.coll_name
                AND c.coll_type != 'linkPoint' ) AS p
    ORDER BY p.type ASC, %s %s
       LIMIT ?
      OFFSET ?"})