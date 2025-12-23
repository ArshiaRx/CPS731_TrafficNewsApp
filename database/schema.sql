-- Traffic News App Database Schema
-- MySQL Database
SET SQL_SAFE_UPDATES = 0;


CREATE DATABASE IF NOT EXISTS trafficnewsapp;
DROP database trafficnewsapp;
select *;

DELETE FROM incidents
WHERE reporter_id = 'TMU';


USE trafficnewsapp;

-- Incidents Table
CREATE TABLE IF NOT EXISTS incidents (
    id VARCHAR(100) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    location VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    description TEXT,
    timestamp DATETIME NOT NULL,
    reporter_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'pending',
    submission_id VARCHAR(100),
    INDEX idx_type (type),
    INDEX idx_severity (severity),
    INDEX idx_status (status),
    INDEX idx_timestamp (timestamp)
);

-- Routes Table
CREATE TABLE IF NOT EXISTS routes (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    radius INT DEFAULT 1000,
    user_id VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
);

-- Submissions Table (for offline queue)
CREATE TABLE IF NOT EXISTS submissions (
    id VARCHAR(100) PRIMARY KEY,
    incident_data TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    INDEX idx_status (status),
    INDEX idx_timestamp (timestamp)
);

INSERT INTO incidents (id,type,severity,location,latitude,longitude,description,timestamp,reporter_id,status) VALUES ('inc_1','accident','high','Highway 401 near Yonge St',43.6532,-79.3832,'Multi-vehicle collision, eastbound lanes blocked. Emergency services on scene.',NOW() - INTERVAL 10 MINUTE,'user123','confirmed'),
('inc_2','construction','medium','Queen St W and Spadina Ave',43.6450,-79.4000,'Road work, expect delays. One lane closed.',NOW() - INTERVAL 30 MINUTE,'cityworker','confirmed'),
('inc_3','hazard','low','Dufferin St south of Bloor St W',43.6500,-79.4200,'Pothole in right lane. Drive carefully.',NOW() - INTERVAL 60 MINUTE,'user456','confirmed'),
('inc_4','accident','critical','Gardiner Expressway at Spadina Ave',43.6400,-79.4000,'Major accident blocking all westbound lanes. Heavy traffic backup.',NOW() - INTERVAL 5 MINUTE,'user789','confirmed'),
('inc_5','closure','high','Bay St between Front St and Wellington St',43.6480,-79.3800,'Road closed due to emergency repair work. Use alternate route.',NOW() - INTERVAL 15 MINUTE,'cityworker','confirmed'),
('inc_6','construction','medium','Bloor St W at Bathurst St',43.6700,-79.4100,'Utility work in progress. Temporary lane restrictions.',NOW() - INTERVAL 45 MINUTE,'worker001','confirmed'),
('inc_7','hazard','medium','Yonge St north of Eglinton Ave',43.7100,-79.4000,'Debris on road. Cleanup in progress.',NOW() - INTERVAL 20 MINUTE,'user234','pending'),
('inc_8','accident','high','Don Valley Parkway southbound near Eglinton',43.7200,-79.3500,'Two-car collision. Right lane blocked.',NOW() - INTERVAL 25 MINUTE,'user567','confirmed'),
('inc_9','construction','low','King St W between Spadina and Bathurst',43.6450,-79.4050,'Minor sidewalk repair. Minimal impact on traffic.',NOW() - INTERVAL 90 MINUTE,'cityworker','confirmed'),
('inc_10','hazard','high','Highway 404 northbound at Sheppard Ave',43.7600,-79.3800,'Large object on highway. Approach with caution.',NOW() - INTERVAL 8 MINUTE,'user890','confirmed'),
('inc_11','accident','medium','University Ave at College St',43.6600,-79.3900,'Fender bender. Minor delays expected.',NOW() - INTERVAL 35 MINUTE,'user111','confirmed'),
('inc_12','closure','medium','Dundas St W at Ossington Ave',43.6550,-79.4200,'Street festival setup. Road partially closed.',NOW() - INTERVAL 120 MINUTE,'eventorg','confirmed'),
('inc_13','construction','high','Highway 427 at Burnhamthorpe Rd',43.6300,-79.5500,'Bridge repair work. Expect significant delays.',NOW() - INTERVAL 50 MINUTE,'cityworker','confirmed'),
('inc_14','hazard','low','St. Clair Ave W at Bathurst St',43.6900,-79.4100,'Small pothole reported. Low priority.',NOW() - INTERVAL 180 MINUTE,'user222','pending'),
('inc_15','accident','critical','Highway 401 westbound at Highway 400',43.7500,-79.5000,'Multi-vehicle pileup. All lanes blocked. Major delays.',NOW() - INTERVAL 3 MINUTE,'user333','confirmed');
