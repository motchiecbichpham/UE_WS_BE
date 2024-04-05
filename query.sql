use miage_web_services;
select * from application;
select * from candidate;
select * from company;
select * from job;
Select * from Application join Job on Application.job_id = job.id where job.company_id = 2;