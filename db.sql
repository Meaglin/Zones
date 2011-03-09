--
-- Tabelstructuur voor tabel `zones`
--

CREATE TABLE IF NOT EXISTS `zones` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `class` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT '1',
  `world` varchar(255) NOT NULL DEFAULT 'world',
  `admins` text,
  `users` text,
  `minz` int(10) DEFAULT NULL,
  `maxz` int(10) DEFAULT NULL,
  `allowwater` int(1) NOT NULL DEFAULT '0',
  `allowlava` int(1) NOT NULL DEFAULT '0',
  `allowdynamite` int(1) NOT NULL DEFAULT '0',
  `enablehealth` int(1) NOT NULL DEFAULT '0',
  `allowmobs` int(1) NOT NULL DEFAULT '0',
  `allowanimals` int(1) NOT NULL DEFAULT '0',
  `size` int(10) DEFAULT '2',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Tabelstructuur voor tabel `zones_vertices`
--

CREATE TABLE IF NOT EXISTS `zones_vertices` (
  `id` int(11) NOT NULL,
  `order` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  PRIMARY KEY (`id`,`order`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;