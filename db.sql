
CREATE TABLE IF NOT EXISTS `zones` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `class` varchar(255) DEFAULT 'ZoneNormal',
  `type` varchar(255) DEFAULT 'ZoneCuboid',
  `world` varchar(255) NOT NULL DEFAULT 'world',
  `admins` text,
  `users` text,
  `minz` int(10) DEFAULT NULL,
  `maxz` int(10) DEFAULT NULL,
  `settings` longtext not null,
  `size` int(10) DEFAULT '2',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `zones_vertices` (
  `id` int(11) NOT NULL,
  `order` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  PRIMARY KEY (`id`,`order`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;