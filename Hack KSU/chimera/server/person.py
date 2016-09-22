from math import pi, sqrt, cos, radians, degrees


class Person:
    EARTH_RADIUS = 6317009  # meters

    def __init__(self, name, phone_number):
        self.__coordinates = tuple
        self.name = name
        self.phone_number = phone_number

    @property
    def coordinates(self):
        return (degrees(self.__coordinates[0]),
                degrees(self.__coordinates[1]))

    @coordinates.setter
    def coordinates(self, new_coordinates):
        self.__coordinates = (radians(new_coordinates[0]),
                              radians(new_coordinates[1]))

    def __matmul__(self, other): # distance operator
        if type(other) is tuple:
            delta_latitude = self.__coordinates[0] - radians(other[0])
            delta_longitude = self.__coordinates[1] - radians(other[1])
            mean_latitude = (self.__coordinates[0] + radians(other[0])) / 2
        else:
            delta_latitude = self.__coordinates[0] - other.__coordinates[0]
            delta_longitude = self.__coordinates[1] - other.__coordinates[1]
            mean_latitude = (self.__coordinates[0] + other.__coordinates[0]) / 2
        return self.EARTH_RADIUS * sqrt(delta_latitude ** 2 + (
            cos(mean_latitude) * delta_longitude) ** 2)


    def __str__(self):
        return "Name: {}\n" \
               "Phone Number: {}\n" \
               "Coordinates: {}".format(self.name, self.phone_number, self.coordinates)
