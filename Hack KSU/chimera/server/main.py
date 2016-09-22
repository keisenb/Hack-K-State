from flask import Flask, request
from person import Person
from twilio.rest import TwilioRestClient

account_sid = str
auth_token = str
to_phone = int
twilio_phone = int
with open("private_data.txt", 'r') as file:
    lines = list(file)
    account_sid = lines[0].rstrip()
    auth_token = lines[1].rstrip()
    twilio_phone = int(lines[2].rstrip())
    to_phone = int(lines[3].rstrip())

client = TwilioRestClient(account_sid, auth_token)

app = Flask(__name__)

GROUP_RADIUS = 5
group = {}
last_lost_group = set()


def send_lost_message(phone_number, lost_person_name):
    message = client.messages.create(to=phone_number, from_=twilio_phone,
                                     body="{} is being left behind!".format(
                                         lost_person_name))
    print("Sent Message: \nto: {}\n'{} is being left behind!'".format(
        phone_number, lost_person_name))


def send_left_behind_message(phone_number):
    message = client.messages.create(to=phone_number, from_=twilio_phone,
                                     body="You have been left behind.")
    print("Sent Message: \nto: {}\n'You have been left behind.'".format(
        phone_number))


def group_center(group: set) -> tuple:
    center = [0, 0]
    for i in group:
        center[0] += i.coordinates[0]
        center[1] += i.coordinates[1]
    center[0] /= len(group)
    center[1] /= len(group)
    return tuple(center)


def find_lost_people(list_of_group_members: list) -> set:
    lost_people = set()
    for person_lost in list_of_group_members:
        i_am_lost = True
        for partner in (set(list_of_group_members) - {person_lost}):
            if person_lost @ group_center(set(list_of_group_members) - {partner}
                                          ) < GROUP_RADIUS:
                i_am_lost = False
        if i_am_lost:
            lost_people.add(person_lost)
    return lost_people


@app.route("/", methods=['GET', 'POST', 'PUT'])
def get_data():
    if request.method == 'PUT':
        group = {}
        return "Data reset"
    if (request.method == 'POST') or (request.method == 'GET'):
        try:
            print(request.args)
            post_data = request.args
            name = request.args['name']
            phone_num = int(request.args['num'])
            lat = float(post_data['lat'])
            long = float(post_data['long'])
            print(name, phone_num, lat, long)
            group[phone_num] = Person(name, phone_num)
            group[phone_num].coordinates = lat, long
        except (ValueError, RuntimeError):
            return '404'
        main()
        return '200'


def main():
    # group = make_test_case()
    lost_people = find_lost_people(group.values())
    for i in lost_people:
        if len(group) > 1:
            print(i)
            for j in (set(group.values()) - {i}):
                send_lost_message(j.phone_number, i.name)
            send_left_behind_message(i.phone_number)
            # last_lost_group = lost_people


if __name__ == "__main__":
    app.run(host='0.0.0.0')
