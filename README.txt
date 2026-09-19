To run with docker:

	run WINDOWS-RUN.bat or LINUX-RUN files

	This will build the project and run 2 requests
		1. POST to load .zip file and parse it to DB. 
		2. GET to get data from db


The final task output should look like this:

===> Running POST Load Command...
  % Total    % Received % Xferd  Average Speed  Time    Time    Time   Current
                                 Dload  Upload  Total   Spent   Left   Speed
  0      0   0      0   0      0      0      0                              0Hibernate: insert into municipalities (name,region_id) values (?,?)
Hibernate: select last_insert_rowid()
Hibernate: insert into municipal_parts (name,part_id,region_id) values (?,?,?)
Hibernate: select last_insert_rowid()
Hibernate: insert into municipal_parts (name,part_id,region_id) values (?,?,?)
Hibernate: select last_insert_rowid()
Hibernate: insert into municipal_parts (name,part_id,region_id) values (?,?,?)
Hibernate: select last_insert_rowid()
Hibernate: insert into municipal_parts (name,part_id,region_id) values (?,?,?)
Hibernate: select last_insert_rowid()
Hibernate: insert into municipal_parts (name,part_id,region_id) values (?,?,?)
Hibernate: select last_insert_rowid()
100    121   0     61 100     60    149    146                              0
{"success":true,"message":"XML data was loaded successfully"}

===> Running GET Municipalities Command...
Hibernate: select m1_0.pk,m1_0.region_id,m1_0.name from municipalities m1_0 limit ? offset ?
Hibernate: select l1_0.region_id,l1_0.pk,l1_0.name,l1_0.part_id from municipal_parts l1_0 where l1_0.region_id=?
{"content":[{"locations":[{"name":"Kopidlno","partId":"69299","regionId":"573060"},{"name":"Ledkov","partId":"69302","regionId":"573060"},{"name":"Mlýnec","partId":"97373","regionId":"573060"},{"name":"Drahoraz","partId":"31801","regionId":"573060"},{"name":"Pševes","partId":"31828","regionId":"573060"}],"name":"Kopidlno","regionId":"573060"}],"empty":false,"first":true,"last":true,"number":0,"numberOfElements":1,"pageable":{"offset":0,"pageNumber":0,"pageSize":20,"paged":true,"sort":{"empty":true,"sorted":false,"unsorted":true},"unpaged":false},"size":20,"sort":{"empty":true,"sorted":false,"unsorted":true},"totalElements":1,"totalPages":1}