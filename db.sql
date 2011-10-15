--
-- Table structure for table `zones`
--

CREATE TABLE IF NOT EXISTS `zones` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `zonetype` varchar(255) DEFAULT NULL,
  `formtype` varchar(255) DEFAULT NULL,
  `world` varchar(255) DEFAULT NULL,
  `admins` longtext,
  `users` longtext,
  `settings` longtext,
  `minz` int(11) DEFAULT NULL,
  `maxz` int(11) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

-- --------------------------------------------------------

--
-- Table structure for table `zones_vertices`
--

CREATE TABLE IF NOT EXISTS `zones_vertices` (
  `id` int(11) NOT NULL,
  `vertexorder` int(11) DEFAULT NULL,
  `x` int(11) DEFAULT NULL,
  `y` int(11) DEFAULT NULL,
  UNIQUE KEY `uq_zones_vertices_1` (`id`,`vertexorder`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
