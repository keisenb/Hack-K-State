from person import Person


def make_test_case():
    test = [Person("bob", 123),
        Person("Alice", 234),
        Person("Smith", 1632),
        Person("John", 1765),
        Person("Zangeef", 697)]
        # ,Person("Zanleef", 697)]
    test[0].coordinates = (39.192511, -96.581802)
    test[1].coordinates = (39.192511, -96.58180)
    test[2].coordinates = (39.19251, -96.581802)
    test[3].coordinates = (39.192511, -96.581302)
    test[4].coordinates = (38.122511, -96.581802)
    # test[5].coordinates = (38.122511, -96.571802)
    return test
